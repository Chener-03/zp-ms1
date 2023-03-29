package xyz.chener.zp.common.config.unifiedReturn.annotation;


import xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.impl.DefaultBase64Encry;

import java.lang.annotation.*;

/**
 * 注意：@JsonSerialize优先级将大于此注解
 */

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryField {

    Class encryClass() default DefaultBase64Encry.class;

    boolean enableEncry() default true;


    String[] hasAnyAuthority() default {};

}
