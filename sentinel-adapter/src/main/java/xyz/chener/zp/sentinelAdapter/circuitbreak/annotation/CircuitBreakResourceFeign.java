package xyz.chener.zp.sentinelAdapter.circuitbreak.annotation;

import xyz.chener.zp.sentinelAdapter.circuitbreak.CircuitBreakDefault;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/10:07
 * @Email: chen@chener.xyz
 */

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CircuitBreakResourceFeign {
    String value() default CircuitBreakDefault.DEFAULT_CITCUIR_BREAK_RESOURCE;
}
