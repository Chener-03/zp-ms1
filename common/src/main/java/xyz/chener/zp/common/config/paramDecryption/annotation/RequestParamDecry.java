package xyz.chener.zp.common.config.paramDecryption.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ValueConstants;
import xyz.chener.zp.common.config.paramDecryption.decryProcess.impl.DefaultParamBase64Decry;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/03/16:43
 * @Email: chen@chener.xyz
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParamDecry {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    boolean required() default true;


    String defaultValue() default ValueConstants.DEFAULT_NONE;

    Class<?> decryClass() default DefaultParamBase64Decry.class;

}
