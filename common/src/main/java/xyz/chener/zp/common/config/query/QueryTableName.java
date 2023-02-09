package xyz.chener.zp.common.config.query;

import java.lang.annotation.*;

/**
 * @Author: chenzp
 * @Date: 2023/02/09/11:19
 * @Email: chen@chener.xyz
 */

/**
 * 用于指定查询的表名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface QueryTableName {
    String value();
}
