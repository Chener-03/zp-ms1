package xyz.chener.zp.common.config.paramDecryption.annotation;

import xyz.chener.zp.common.config.paramDecryption.decryProcess.impl.DefaultParamBase64Decry;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/11:26
 * @Email: chen@chener.xyz
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryField {

    boolean required() default true;

    Class<?> decryClass() default DefaultParamBase64Decry.class;
}
