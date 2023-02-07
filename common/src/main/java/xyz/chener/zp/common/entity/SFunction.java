package xyz.chener.zp.common.entity;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @Author: chenzp
 * @Date: 2023/02/02/13:02
 * @Email: chen@chener.xyz
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}