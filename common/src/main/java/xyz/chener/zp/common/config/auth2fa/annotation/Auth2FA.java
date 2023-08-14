package xyz.chener.zp.common.config.auth2fa.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth2FA {

    // 是否强制要求用户使用2fa验证
    boolean require() default false;
}
