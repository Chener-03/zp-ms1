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
import xyz.chener.zp.common.config.paramDecryption.core.RequestBodyDecryResolver;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryResult;
import xyz.chener.zp.common.config.unifiedReturn.encry.core.EncryCore;
import xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.EncryInterface;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
public class UnifiedReturnHandle  implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Annotation[] methodAnnotations = returnType.getMethodAnnotations();
        Annotation[] annotations = returnType.getDeclaringClass().getAnnotations();
        return containAnn(methodAnnotations, UnifiedReturn.class.getName())
                || containAnn(annotations, UnifiedReturn.class.getName());
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

            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            resp.setStatus(R.HttpCode.HTTP_OK.get());
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String json = "";
            ObjectMapper om = new ObjectMapper();

            if ((returnType.hasMethodAnnotation(EncryResult.class)
                    || returnType.getDeclaringClass().isAnnotationPresent(EncryResult.class))
                    && returnValue != null){

                if (ObjectUtils.isBasicType(returnValue.getClass())){
                    EncryField encryField = returnType.getMethodAnnotation(EncryField.class);
                    if (encryField != null){
                        try {
                            EncryInterface instance = (EncryInterface) encryField.encryClass().getConstructor().newInstance();
                            returnValue = instance.encry(returnValue.toString(),encryField);
                            if (encryField.hasAnyAuthority().length>0){
                                log.warn("权限划分数据只适用于返回实体对象，不适用于返回基本类型");
                            }
                        }catch (Exception ignored){}
                    }
                }else {
                    SimpleModule sm = new SimpleModule();
                    Class[] allType = {String.class,Integer.class,Long.class
                            ,Double.class,Float.class,Boolean.class,Short.class
                            , BigDecimal.class, BigInteger.class,Date.class};
                    for (Class type : allType) {
                        sm.addSerializer(type, new EncryCore.EncryJacksonSerializerDispatch<>(type));
                    }
                    om.registerModule(sm);
                }
            }


            SimpleModule sm = new SimpleModule();
            om.registerModule(sm);
            if (returnValue instanceof R)
            {
                json = om.writeValueAsString(returnValue);
            }else
            {
                json = om.writeValueAsString(R.Builder.getInstance().setCode(R.HttpCode.HTTP_OK.get())
                        .setObj(returnValue).build());
            }

            StreamUtils.copy(json.getBytes(StandardCharsets.UTF_8),os);
        }catch (Exception openOutputError){
            log.info("无法打开输出流,可能已经通过Response返回,如果是这样,请忽略此异常,并且尽量不要使用统一返回");
        }
    }





}