package xyz.chener.zp.common.config.cache.error;

public class RedissonClientNotFoundError extends RuntimeException{
    public RedissonClientNotFoundError() {
        super("spring上下文未找到RedissonClient，Mybatis缓存异常");
    }
}
