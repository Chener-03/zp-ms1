package xyz.chener.zp.common.config.unifiedReturn.annotation;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/01/29/11:57
 * @Email: chen@chener.xyz
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DispatchException {

    Class<? extends Throwable>[] value() default {};

    Class<?>[] otherParams() default {};

}