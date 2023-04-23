package xyz.chener.zp.common.config.unifiedReturn.handle;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/04/23/16:15
 * @Email: chen@chener.xyz
 */
public interface ResultObjectCovertInterface {

    boolean support(Object returnValue, MethodParameter returnType, HttpServletResponse resp);


    void process(Object returnValue, MethodParameter returnType, HttpServletResponse resp, ServletOutputStream os);


}
