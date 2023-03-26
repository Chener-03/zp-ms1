package xyz.chener.zp.zpstoragecalculation.controller;


import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class RedisCacheController {

    private final RedissonClient redissonClient;
    private final CommonConfig commonConfig;


    public RedisCacheController(RedissonClient redissonClient, CommonConfig commonConfig) {
        this.redissonClient = redissonClient;
        this.commonConfig = commonConfig;
    }


    @DeleteMapping("/clearRedisCaches")
    public Long clearRedisCaches(@RequestParam(value = "key", required = false) String key) {
        if (StringUtils.hasText(key)){
            return redissonClient.getKeys().deleteByPattern(commonConfig.getMybatisCache().getRedisCachePrefix() + key + "*");
        }
        return redissonClient.getKeys().deleteByPattern(commonConfig.getMybatisCache().getRedisCachePrefix() + "*");
    }

}
