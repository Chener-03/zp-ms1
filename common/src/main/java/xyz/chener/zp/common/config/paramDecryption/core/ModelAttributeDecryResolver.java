package xyz.chener.zp.common.config.paramDecryption.core;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.config.paramDecryption.annotation.DecryField;
import xyz.chener.zp.common.config.paramDecryption.annotation.ModelAttributeDecry;
import xyz.chener.zp.common.config.paramDecryption.decryProcess.DecryInterface;
import xyz.chener.zp.common.config.paramDecryption.error.ParamBindError;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/11:29
 * @Email: chen@chener.xyz
 */
@Slf4j
public class ModelAttributeDecryResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        ModelAttributeDecry parameterAnnotation = parameter.getParameterAnnotation(ModelAttributeDecry.class);
        if (parameterAnnotation == null) {
            return false;
        }
        if (ObjectUtils.isBasicType(parameterType) || parameterType.equals(Date.class)) {
            log.warn("ModelAttributeDecryResolver 不支持基本类型参数解析,参数位置[{}.{}.{}]"
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
        ModelAttributeDecry modelAttributeDecry = parameter.getParameterAnnotation(ModelAttributeDecry.class);
        Map<String, DecryInterface> decryInstanceCache = new HashMap<>();

        Arrays.stream(parameter.getParameterType().getMethods())
                .filter(method -> method.getName().startsWith("set")
                        && method.getParameterCount() == 1
                        && (ObjectUtils.isBasicType(method.getParameterTypes()[0])
                        || method.getParameterTypes()[0].equals(Date.class)))
                .forEach(method -> {
                    String fieldName = method.getName().substring(3);
                    fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                    final String finalFieldName = fieldName;
                    Optional.ofNullable(webRequest.getParameter(fieldName))
                            .ifPresent(value -> {
                                try {
                                    if (res.get() == null){
                                        res.set(parameter.getParameterType().getConstructor().newInstance());
                                    }
                                    Field field = method.getDeclaringClass().getDeclaredField(finalFieldName);
                                    method.invoke(res.get(), convert(method.getParameterTypes()[0], field,modelAttributeDecry, value,decryInstanceCache));
                                } catch (Exception exception) {
                                    throw new ParamBindError(finalFieldName,exception);
                                }
                            });
                });


        Validated validatedAnn = parameter.getParameterAnnotation(Validated.class);
        if (validatedAnn != null) {
            if (res.get() == null){
                res.set(ObjectUtils.newInstance(parameter.getParameterType()));
            }
            Validator validator = ApplicationContextHolder.getApplicationContext().getBean(Validator.class);
            String name = Conventions.getVariableNameForParameter(parameter);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(res.get(), name);
            validator.validate(res.get(), bindingResult);
            if (bindingResult.hasErrors()) {
                throw new MethodArgumentNotValidException(parameter, bindingResult);
            }
        }
        return res.get();
    }


    private Object convert(Class<?> targetType, Field targetField
            ,ModelAttributeDecry modelAttributeDecry, String value
            ,Map<String, DecryInterface> decryInstanceCache) throws ParseException {
        Class<?> clz = modelAttributeDecry.decryClass();
        DecryField decryField = targetField.getAnnotation(DecryField.class);
        DecryInterface decryInterface = null;
        if (decryField != null && decryField.required()){
            decryInterface = buildDecryInterfaceWithCache( decryField.decryClass(), decryInstanceCache);
        }else if (decryField == null) {
            decryInterface = buildDecryInterfaceWithCache( clz, decryInstanceCache);
        }
        if (Objects.nonNull(decryInterface)){
            value = decryInterface.decry(value);
        }

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
            DateTimeFormat dateTimeFormat = targetField.getAnnotation(DateTimeFormat.class);
            if (dateTimeFormat!=null){
                return new SimpleDateFormat(dateTimeFormat.pattern()).parse(value);
            }
            return new Date(Long.parseLong(value));
        }
        return null;
    }

    private DecryInterface buildDecryInterfaceWithCache(Class clazz,Map<String, DecryInterface> decryInstanceCache){
        try {
            if (!decryInstanceCache.containsKey(clazz.getName())){
                Object instance = clazz.getConstructor().newInstance();
                decryInstanceCache.put(clazz.getName(), (DecryInterface) instance);
            }
            return decryInstanceCache.get(clazz.getName());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }




}
