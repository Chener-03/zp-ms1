package xyz.chener.zp.common.config.dynamicVerification.annotation;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/02/03/10:57
 * @Email: chen@chener.xyz
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DsTargetField {

    String[] value() default {};

    int order() default Integer.MAX_VALUE;

}
