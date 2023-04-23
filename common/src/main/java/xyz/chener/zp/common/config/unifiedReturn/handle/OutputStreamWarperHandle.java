package xyz.chener.zp.common.config.unifiedReturn.handle;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import xyz.chener.zp.common.config.unifiedReturn.warper.OutputStreamWarper;
import xyz.chener.zp.common.config.unifiedReturn.warper.proxy.HttpServletResponseIgnoredOsProxy;

import java.lang.reflect.Proxy;

/**
 * @Author: chenzp
 * @Date: 2023/04/23/16:22
 * @Email: chen@chener.xyz
 */

@Slf4j
public class OutputStreamWarperHandle implements ResultObjectCovertInterface{
    @Override
    public boolean support(Object returnValue, MethodParameter returnType, HttpServletResponse resp) {
        return returnValue instanceof OutputStreamWarper;
    }

    @Override
    public void process(Object returnValue, MethodParameter returnType, HttpServletResponse resp, ServletOutputStream os) {
        if (returnValue instanceof OutputStreamWarper wp){
            wp.setResponse((HttpServletResponse) Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[]{HttpServletResponse.class},
                    new HttpServletResponseIgnoredOsProxy(resp)));
            try {
                wp.getInputStream().transferTo(os);
                wp.getInputStream().close();
            }catch (Exception e){
                log.error("无法从OutputStreamWarper中获取InputStream,可能是因为没有实现getInputStream方法,或者返回的InputStream为null");
            }
        }
    }
}
