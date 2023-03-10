package xyz.chener.zp.common.config.paramDecryption.core;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import xyz.chener.zp.common.config.paramDecryption.annotation.DecryField;
import xyz.chener.zp.common.config.paramDecryption.annotation.ModelAttributeDecry;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestBodyDecry;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/11:30
 * @Email: chen@chener.xyz
 */
@Slf4j
public class RequestBodyDecryResolver implements HandlerMethodArgumentResolver {

    public static final String REQUEST_BODY_SOURCE_DATA = "requestBodySourceData";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        RequestBodyDecry parameterAnnotation = parameter.getParameterAnnotation(RequestBodyDecry.class);
        if (parameterAnnotation == null) {
            return false;
        }
        if (ObjectUtils.isBasicType(parameterType) || parameterType.equals(Date.class)) {
            log.warn("RequestBodyDecryResolver 不支持基本类型参数解析,参数位置[{}.{}.{}]"
                    ,parameter.getDeclaringClass().getName()
                    ,parameter.getMethod()==null?"":parameter.getMethod().getName()
                    ,parameter.getParameterName());
            return false;
        }
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        AtomicReference<Object> res = new AtomicReference<>(null);
        RequestBodyDecry parameterAnnotation = parameter.getParameterAnnotation(RequestBodyDecry.class);
        if (webRequest.getNativeRequest() instanceof HttpServletRequest request) {
            ByteArrayOutputStream bios = new ByteArrayOutputStream();
            request.getInputStream().transferTo(bios);
            bios.close();
            final String sourceData = bios.toString(StandardCharsets.UTF_8);
            request.setAttribute(REQUEST_BODY_SOURCE_DATA, sourceData);
            return buildObjectMapper(parameterAnnotation).readValue(sourceData, parameter.getParameterType());
        }else {
            log.warn("webRequest 不是 HttpServletRequest???");
        }
        return res.get();
    }


    private ObjectMapper buildObjectMapper(RequestBodyDecry parameterAnnotation) {
        ObjectMapper om = new ObjectMapper();
        SimpleModule sm = new SimpleModule();
        sm.addDeserializer(String.class, new DecryJacksonDeserializerDipatcher(parameterAnnotation));
        om.registerModule(sm);
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return om;
    }


    private static class DecryJacksonDeserializerDipatcher extends JsonDeserializer<String> implements ContextualDeserializer {

        private final RequestBodyDecry requestBodyDecry;


        public DecryJacksonDeserializerDipatcher(RequestBodyDecry requestBodyDecry) {
            this.requestBodyDecry = requestBodyDecry;
        }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            return null;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            if (ObjectUtils.nullSafeEquals(property.getType().getRawClass(),String.class)) {
                return new DecryJacksonDeserializer(requestBodyDecry
                        ,property.getAnnotation(DecryField.class)
                        ,property.getType().getRawClass());
            }
            return ctxt.findContextualValueDeserializer(property.getType(), property);
        }
    }


    private static class DecryJacksonDeserializer extends JsonDeserializer<Object> {

        private final DecryField decryField;
        private final RequestBodyDecry requestBodyDecry;

        private final Class targetClass;

        public DecryJacksonDeserializer(RequestBodyDecry requestBodyDecry,DecryField decryField,Class targetClass) {
            this.requestBodyDecry = requestBodyDecry;
            this.decryField = decryField;
            this.targetClass = targetClass;
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            return p.getText();
        }
    }

}