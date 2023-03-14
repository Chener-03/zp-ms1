package xyz.chener.zp.common.config.antiShaking.annotation;

import xyz.chener.zp.common.config.antiShaking.processer.AntiShakingInterface;
import xyz.chener.zp.common.config.antiShaking.processer.impl.DefaultRedisAntiShaking;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/10:47
 * @Email: chen@chener.xyz
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AntiShaking {

    boolean hasUserAuth() default true;

    int limitTimeMs() default 1000;

    String[] validParams() default {};

    Class<? extends AntiShakingInterface> processer() default DefaultRedisAntiShaking.class;

}
