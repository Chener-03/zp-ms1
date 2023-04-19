package xyz.chener.zp.common.config.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.ibatis.cache.Cache;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.cache.error.RedissonClientNotFoundError;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ThreadUtils;
import xyz.chener.zp.common.utils.TickNotification;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultRedissonMybatisCache implements Cache {

    private volatile static com.google.common.cache.Cache<String, String> cb = null;

    private static void initializationDelayDelete(){
        if (cb == null){
            synchronized (DefaultRedissonMybatisCache.class){
                if (cb == null){
                    cb = CacheBuilder.newBuilder()
                            .expireAfterAccess(Duration.ofSeconds(1))
                            .removalListener((RemovalListener<String, String>) notification -> {
                                if (notification.getCause().toString().equals("EXPIRED") && notification.getKey() != null) {
                                    ThreadUtils.runIgnoreException(()->{
                                        RedissonClient c = ApplicationContextHolder.getApplicationContext().getBean(RedissonClient.class);
                                        c.getBucket(notification.getKey()).deleteAsync();
                                    });
                                }
                            })
                            .build();
                    TickNotification.getInstance().addRunnable(()-> ThreadUtils.runIgnoreException(()-> cb.cleanUp()));
                }
            }
        }
    }


    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final String id;

    private volatile RedissonClient redissonClient = null;
    private volatile CommonConfig commonConfig = null;

    public DefaultRedissonMybatisCache(String id) {
        this.id = id;
    }

    private RedissonClient getRedissonClient(){
        if (redissonClient == null) {
            synchronized (this) {
                if (redissonClient == null) {
                    redissonClient = ApplicationContextHolder.getApplicationContext().getBean(RedissonClient.class);
                    commonConfig = ApplicationContextHolder.getApplicationContext().getBean(CommonConfig.class);
                    if (commonConfig.getMybatisCache().getEnableDealyDelete()) {
                        initializationDelayDelete();
                    }
                    AssertUrils.state(redissonClient!=null,new RedissonClientNotFoundError());
                }
            }
        }
        return redissonClient;
    }

    private String getKey(){
        return commonConfig.getMybatisCache().getRedisCachePrefix() + id + ":";
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        RedissonClient client = getRedissonClient();
        Integer expireMs = commonConfig.getMybatisCache().getRedisCacheExpireMs();
        client.getBucket(getKey() + key.toString()).set(value,expireMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object getObject(Object key) {
        RedissonClient client = getRedissonClient();
        return client.getBucket(getKey() + key.toString()).get();
    }

    @Override
    public Object removeObject(Object key) {
        RedissonClient client = getRedissonClient();
        if(cb != null){
            cb.put(getKey() + key.toString(),getKey() + key.toString());
        }
        return client.getBucket(getKey() + key.toString()).getAndDelete();
    }

    @Override
    public void clear() {
        RedissonClient client = getRedissonClient();
        client.getKeys().deleteByPattern(getKey() + "*");
    }

    @Override
    public int getSize() {
        RedissonClient client = getRedissonClient();
        Iterable<String> keys = client.getKeys().getKeysByPattern(getKey() + "*");
        AtomicInteger size = new AtomicInteger(0);
        keys.forEach(key -> size.incrementAndGet());
        return size.get();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }
}
