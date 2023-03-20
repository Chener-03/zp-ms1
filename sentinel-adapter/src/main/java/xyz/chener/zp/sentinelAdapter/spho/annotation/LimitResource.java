package xyz.chener.zp.sentinelAdapter.spho.annotation;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/14:17
 * @Email: chen@chener.xyz
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitResource {
    String value();
}
