package xyz.chener.zp.common.config.unifiedReturn.warper.proxy;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author: chenzp
 * @Date: 2023/04/23/10:00
 * @Email: chen@chener.xyz
 */

@Slf4j
public class HttpServletResponseIgnoredOsProxy implements InvocationHandler {

    private final HttpServletResponse response;

    public HttpServletResponseIgnoredOsProxy(HttpServletResponse response) {
        this.response = response;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(proxy instanceof HttpServletResponse){
            if(method.getName().equals("getOutputStream") || method.getName().equals("getWriter")){
                log.warn("禁止在 OutputStreamWarper 中获取输出流进行输出,请到 getInputStream 中进行输出！");
                return null;
            }
            return method.invoke(response,args);
        }
        log.warn(" 不是 HttpServletResponse 的代理对象 ");
        return method.invoke(response,args);
    }
}
