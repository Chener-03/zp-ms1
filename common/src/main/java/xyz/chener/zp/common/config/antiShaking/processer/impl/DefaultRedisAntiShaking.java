package xyz.chener.zp.common.config.antiShaking.processer.impl;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import xyz.chener.zp.common.config.antiShaking.processer.AntiShakingInterface;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/10:52
 * @Email: chen@chener.xyz
 */

@Slf4j
public class DefaultRedisAntiShaking implements AntiShakingInterface {

    private static final String ANTI_SHAKING = "antiShakingFilter:";

    @Override
    public Boolean check(String key, int limitTimeMs) {
        key = ANTI_SHAKING + key;
        RedissonClient redissonClient = null;
        try {
            redissonClient = ApplicationContextHolder.getApplicationContext().getBean(RedissonClient.class);
        }catch (Exception exception){
            log.warn("redissonClient not found");
            return true;
        }
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return false;
        }
        bucket.set("1",limitTimeMs, TimeUnit.MILLISECONDS);
        return true;
    }
}
