package xyz.chener.zp.common.config.paramDecryption.annotation;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.chener.zp.common.config.paramDecryption.decryProcess.impl.DefaultParamBase64Decry;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/10:44
 * @Email: chen@chener.xyz
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBodyDecry {
    boolean required() default true;

    Class<?> decryClass() default DefaultParamBase64Decry.class;

}
