package xyz.chener.zp.common.config.paramDecryption.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import xyz.chener.zp.common.config.paramDecryption.annotation.ModelAttributeDecry;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.common.config.paramDecryption.decryProcess.DecryInterface;
import xyz.chener.zp.common.config.paramDecryption.error.ParamBindError;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/11:30
 * @Email: chen@chener.xyz
 */

@Slf4j
public class RequestParamDecryResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        RequestParamDecry requestParamDecry = parameter.getParameterAnnotation(RequestParamDecry.class);
        if (requestParamDecry == null) {
            return false;
        }
        if (!ObjectUtils.isBasicType(parameterType) && !parameterType.equals(Date.class)) {
            log.warn("RequestParamDecryResolver 只支持基本类型解析,参数位置[{}.{}.{}]"
                    ,parameter.getDeclaringClass().getName()
                    ,parameter.getMethod()==null?"":parameter.getMethod().getName()
                    ,parameter.getParameterName());
            return false;
        }
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        RequestParamDecry requestParamDecry = parameter.getParameterAnnotation(RequestParamDecry.class);
        String parameterName = parameter.getParameterName();
        if (StringUtils.hasText(requestParamDecry.value()))
        {
            parameterName = requestParamDecry.value();
        }

        String parameterValue = webRequest.getParameter(parameterName);
        if (parameterValue == null && requestParamDecry.required()) {
            throw new MissingServletRequestParameterException(parameterName, parameter.getParameterType().getName());
        }
        if (parameterValue == null) {
            parameterValue = requestParamDecry.defaultValue();
            if (parameterValue.equals(ValueConstants.DEFAULT_NONE))
                return null;
            return parameterValue;
        }
        DecryInterface instance = (DecryInterface) requestParamDecry.decryClass().getConstructor().newInstance();
        String decryValue = instance.decry(parameterValue);
        try {
            return convert(decryValue, parameter.getParameterType());
        }catch (Exception exception ){
            throw new ParamBindError(parameterName,exception);
        }
    }

    private Object convert(String value, Class targetType)  {
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
        return null;
    }

}