package xyz.chener.zp.common.config.antiShaking.processer.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import xyz.chener.zp.common.config.antiShaking.processer.AntiShakingInterface;

import java.time.Duration;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/14:38
 * @Email: chen@chener.xyz
 */
public class DefaultNativeAntiShaking implements AntiShakingInterface {

    private static final Cache<Object, Object> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(Duration.ofSeconds(1))
            .build();

    @Override
    public Boolean check(String key, int limitTimeMs) {
        if (cache.getIfPresent(key) != null) {
            return false;
        }
        cache.put(key, true);
        return true;
    }
}
