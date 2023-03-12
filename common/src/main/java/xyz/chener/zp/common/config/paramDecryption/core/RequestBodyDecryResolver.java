package xyz.chener.zp.common.config.paramDecryption.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
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
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestBodyDecry;
import xyz.chener.zp.common.config.paramDecryption.decryProcess.DecryInterface;
import xyz.chener.zp.common.config.paramDecryption.error.ParamBindError;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
        ConcurrentHashMap<String, DecryInterface> cacheMap = new ConcurrentHashMap<>();
        Class[] allType = {String.class,Integer.class,Long.class
                ,Double.class,Float.class,Boolean.class,Short.class,Byte.class
                ,Character.class,BigDecimal.class,Date.class};
        for (Class type : allType) {
            sm.addDeserializer(type, new DecryJacksonDeserializerDispatch(parameterAnnotation
                    ,cacheMap,type));
        }
        om.registerModule(sm);
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return om;
    }


    private static class DecryJacksonDeserializerDispatch<T> extends JsonDeserializer<T> implements ContextualDeserializer {

        private final RequestBodyDecry requestBodyDecry;

        private final ConcurrentHashMap<String, DecryInterface> cache;

        private final Class dispatchClass;


        public DecryJacksonDeserializerDispatch(RequestBodyDecry requestBodyDecry
                , ConcurrentHashMap<String, DecryInterface> cache,Class dispatchClass) {
            this.requestBodyDecry = requestBodyDecry;
            this.cache = cache;
            this.dispatchClass = dispatchClass;
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            return null;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            if (ObjectUtils.nullSafeEquals(property.getType().getRawClass(),dispatchClass)) {
                Class declaringClass = property.getMember().getDeclaringClass();
                String fieldName = property.getName();
                Field field = null;
                try {
                    field = declaringClass.getDeclaredField(fieldName);
                }catch (Exception exception){
                    throw new RuntimeException("实体类字段缺失 "+declaringClass.getName()+"."+fieldName);
                }
                return new DecryJacksonDeserializer(requestBodyDecry
                        ,field
                        ,property.getType().getRawClass(),cache);
            }
            return ctxt.findContextualValueDeserializer(property.getType(), property);
        }
    }


    private static class DecryJacksonDeserializer extends JsonDeserializer<Object> {

        private final Field field;
        private final RequestBodyDecry requestBodyDecry;
        private final Class targetClass;
        private final Map<String, DecryInterface> cache;

        public DecryJacksonDeserializer(RequestBodyDecry requestBodyDecry
                , Field field
                , Class targetClass, ConcurrentHashMap<String, DecryInterface> cache) {
            this.requestBodyDecry = requestBodyDecry;
            this.field = field;
            this.targetClass = targetClass;
            this.cache = cache;
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String sourceText = p.getText();
            Class<?> decryClass = requestBodyDecry.decryClass();
            DecryField decryField = field.getAnnotation(DecryField.class);
            if(decryField != null){
                decryClass = decryField.decryClass();
                if (!decryField.required()){
                    decryClass = null;
                }
            }
            DecryInterface decryInterface = null;
            if (decryClass != null && (decryInterface = getDecryInstance(decryClass)) != null){
                sourceText = decryInterface.decry(sourceText);
            }
            try {
                return convert(sourceText,targetClass,field);
            } catch (ParseException e) {
                throw new ParamBindError(field.getName(),e);
            }
        }

        private Object convert(String value, Class targetType,Field targetField) throws ParseException {
            if (targetType.equals(String.class)) {
                return value;
            }
            if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
                return Integer.parseInt(value);
            }
            if (targetType.equals(Long.class) || targetType.equals(long.class)) {
                return Long.parseLong(value);
            }
            if (targetType.equals(Double.class) || targetType.equals(double.class)) {
                return Double.parseDouble(value);
            }
            if (targetType.equals(Float.class) || targetType.equals(float.class)) {
                return Float.parseFloat(value);
            }
            if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
                return Boolean.parseBoolean(value);
            }
            if (targetType.equals(Short.class) || targetType.equals(short.class)) {
                return Short.parseShort(value);
            }
            if (targetType.equals(Byte.class) || targetType.equals(byte.class)) {
                return Byte.parseByte(value);
            }
            if (targetType.equals(Character.class) || targetType.equals(char.class)) {
                return value.charAt(0);
            }
            if (targetType.equals(BigDecimal.class)){
                return new BigDecimal(value);
            }
            if (targetType.equals(Date.class)) {
                JsonFormat jsonFormat = targetField.getAnnotation(JsonFormat.class);
                if (jsonFormat!=null){
                    return new SimpleDateFormat(jsonFormat.pattern()).parse(value);
                }
                return new Date(Long.parseLong(value));
            }
            return null;
        }


        private DecryInterface getDecryInstance(Class<?> decryClass){
            if (cache.containsKey(decryClass.getName())){
                return cache.get(decryClass.getName());
            }
            try {
                Constructor<?> constructor = decryClass.getConstructor();
                DecryInterface decryInstance = (DecryInterface) constructor.newInstance();
                cache.put(decryClass.getName(),decryInstance);
                return decryInstance;
            } catch ( Exception e) {
                log.error("{}实例化失败,{}",decryClass,e);
            }
            return null;
        }


    }

}