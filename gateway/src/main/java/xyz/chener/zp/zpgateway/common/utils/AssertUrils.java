package xyz.chener.zp.zpgateway.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: chenzp
 * @Date: 2023/01/16/11:32
 * @Email: chen@chener.xyz
 */
@Slf4j
public class AssertUrils {

    public static void state(boolean expression,Class<?> exceptionClass)
    {
        if (!expression)
        {
            Object o = null;
            try {
                o = exceptionClass.getConstructor().newInstance();
            } catch (Exception e) {
                log.error("ExceptionClass Must have non-parameter constructor!!");
                throw new RuntimeException(e);
            }
            throw (RuntimeException) o;
        }
    }

    public static void state(boolean expression,RuntimeException exception)
    {
        if (!expression) throw exception;
    }

}
