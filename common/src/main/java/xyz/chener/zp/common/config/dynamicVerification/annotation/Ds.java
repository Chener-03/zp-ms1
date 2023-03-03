package xyz.chener.zp.common.config.dynamicVerification.annotation;

import xyz.chener.zp.common.config.dynamicVerification.rules.impl.DefaultDynamicVer;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/02/03/10:56
 * @Email: chen@chener.xyz
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ds {

    String value();

    Class<?> verImplClass() default DefaultDynamicVer.class;

}
