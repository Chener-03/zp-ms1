package xyz.chener.zp.common.config.paramDecryption.annotation;

import org.springframework.core.annotation.AliasFor;
import xyz.chener.zp.common.config.paramDecryption.decryProcess.impl.DefaultParamBase64Decry;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/10:45
 * @Email: chen@chener.xyz
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelAttributeDecry {
    Class<?> decryClass() default DefaultParamBase64Decry.class;
}
