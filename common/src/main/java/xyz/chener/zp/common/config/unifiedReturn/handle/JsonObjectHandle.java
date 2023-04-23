package xyz.chener.zp.common.config.unifiedReturn.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryResult;
import xyz.chener.zp.common.config.unifiedReturn.encry.core.EncryCore;
import xyz.chener.zp.common.config.unifiedReturn.encry.encryProcess.EncryInterface;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/04/23/16:24
 * @Email: chen@chener.xyz
 */

@Slf4j
public class JsonObjectHandle implements ResultObjectCovertInterface{
    @Override
    public boolean support(Object returnValue, MethodParameter returnType, HttpServletResponse resp) {
        return true;
    }

    @Override
    public void process(Object returnValue, MethodParameter returnType, HttpServletResponse resp, ServletOutputStream os) {
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
                        , BigDecimal.class, BigInteger.class, Date.class};
                for (Class type : allType) {
                    sm.addSerializer(type, new EncryCore.EncryJacksonSerializerDispatch<>(type));
                }
                om.registerModule(sm);
            }
        }

        try {
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
            StreamUtils.copy(json.getBytes(StandardCharsets.UTF_8), os);
        }catch (Exception exception){
            log.error("无法将返回值转换为json格式");
        }
    }
}
