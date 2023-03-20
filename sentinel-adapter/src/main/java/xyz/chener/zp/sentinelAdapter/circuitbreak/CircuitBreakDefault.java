package xyz.chener.zp.sentinelAdapter.circuitbreak;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/10:23
 * @Email: chen@chener.xyz
 */


public class CircuitBreakDefault implements ApplicationListener<ApplicationStartedEvent> {

    public static final String DEFAULT_CITCUIR_BREAK_RESOURCE = "Sentinel-Openfeign:CircuitBreakResourceDefaultKey:";


    public static void LoadDefaultCircleBreakRule() {
        DegradeRule degradeRule = new DegradeRule(DEFAULT_CITCUIR_BREAK_RESOURCE)
                .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                .setCount(0.5)
                .setTimeWindow(60)
                .setMinRequestAmount(5)
                .setStatIntervalMs(1000 * 60);
        CircuitBreakRuleManager.addRules(DEFAULT_CITCUIR_BREAK_RESOURCE,degradeRule);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        LoadDefaultCircleBreakRule();
    }
}
