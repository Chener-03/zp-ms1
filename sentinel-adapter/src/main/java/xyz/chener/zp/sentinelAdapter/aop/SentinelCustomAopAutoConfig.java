package xyz.chener.zp.sentinelAdapter.aop;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CircuitBreakAop.class, CurrentLimitAop.class})
public class SentinelCustomAopAutoConfig {
}
