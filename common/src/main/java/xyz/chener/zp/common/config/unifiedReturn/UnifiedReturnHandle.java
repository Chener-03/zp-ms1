package xyz.chener.zp.common.config.unifiedReturn;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryResult;
import xyz.chener.zp.common.config.unifiedReturn.encry.core.EncryCore;
import xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.EncryInterface;
import xyz.chener.zp.common.config.unifiedReturn.handle.JsonObjectHandle;
import xyz.chener.zp.common.config.unifiedReturn.handle.OutputStreamWarperHandle;
import xyz.chener.zp.common.config.unifiedReturn.handle.ResultObjectCovertInterface;
import xyz.chener.zp.common.config.unifiedReturn.warper.OutputStreamWarper;
import xyz.chener.zp.common.config.unifiedReturn.warper.proxy.HttpServletResponseIgnoredOsProxy;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.utils.LoggerUtils;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Slf4j
public class UnifiedReturnHandle  implements HandlerMethodReturnValueHandler {

    private final static List<ResultObjectCovertInterface> resultObjectCovertHandleList = List.of(
            new OutputStreamWarperHandle(),
            new JsonObjectHandle()
    );

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Annotation[] methodAnnotations = returnType.getMethodAnnotations();
        Annotation[] annotations = returnType.getDeclaringClass().getAnnotations();
        return (containAnn(methodAnnotations, UnifiedReturn.class.getName())
                || containAnn(annotations, UnifiedReturn.class.getName()));
    }

    private boolean containAnn(Annotation[] anns,String annName)
    {
        for (Annotation ann : anns) {
            if (ann.annotationType().getName().equals(annName))
                return true;
        }
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse resp = webRequest.getNativeResponse(HttpServletResponse.class);

        try (ServletOutputStream os = resp.getOutputStream()){

            for (ResultObjectCovertInterface handle : resultObjectCovertHandleList) {
                if (handle.support(returnValue, returnType, resp)){
                    handle.process(returnValue, returnType, resp, os);
                    break;
                }
            }

        }catch (Exception openOutputError){
            if (openOutputError instanceof IllegalStateException){
                log.info("无法打开输出流,可能已经通过Response返回,如果是这样,请忽略此异常,并且尽量不要使用统一返回");
            }else {
                LoggerUtils.logErrorStackTrace(openOutputError,log);
            }
        }
    }

}