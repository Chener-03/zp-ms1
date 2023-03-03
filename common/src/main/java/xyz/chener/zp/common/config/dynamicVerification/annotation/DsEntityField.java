package xyz.chener.zp.common.config.dynamicVerification.annotation;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/03/14:34
 * @Email: chen@chener.xyz
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DsEntityField {
    int order() default Integer.MAX_VALUE;
}
