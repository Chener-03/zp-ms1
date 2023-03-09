package xyz.chener.zp.common.config.query.annotation;

/**
 * @Author: chenzp
 * @Date: 2023/03/09/15:00
 * @Email: chen@chener.xyz
 */

import java.lang.annotation.*;

/**
 * 查询必须要的字段
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface QueryMustField {
}
