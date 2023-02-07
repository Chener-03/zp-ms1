package xyz.chener.zp.common.config.dynamicVerification.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.dynamicVerification.annotation.Ds;
import xyz.chener.zp.common.config.dynamicVerification.annotation.DsTargetField;
import xyz.chener.zp.common.config.dynamicVerification.error.DynamicVerificationError;
import xyz.chener.zp.common.config.dynamicVerification.rules.DynamicVerRuleInterface;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chenzp
 * @Date: 2023/02/03/11:07
 * @Email: chen@chener.xyz
 */

@Aspect
@Slf4j
public class DynamicVerAop {

    private final CommonConfig commonConfig;

    private final ConcurrentHashMap<String, String[]> methodParamCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DynamicVerRuleInterface> dsInstanceCache = new ConcurrentHashMap<>();

    public DynamicVerAop(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Pointcut("@annotation(xyz.chener.zp.common.config.dynamicVerification.annotation.Ds)" +
            " && (@annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping))")
    public void dsPointcut() { }



    @Around("dsPointcut()")
    public Object dsAround(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Signature signature = pjp.getSignature();
            if (signature instanceof MethodSignature methodSignature)
            {
                Method targetMethod = methodSignature.getMethod();
                Ds dsAnn = targetMethod.getAnnotation(Ds.class);
                String dsValueFieldName = dsAnn.value();
                Class<?> dsClazz = dsAnn.verImplClass();
                if (StringUtils.hasText(dsValueFieldName))
                {
                    Set<DsFieldsMetadata> metadataSet = getMethodDsFieldsMetadata(targetMethod);
                    DynamicVerRuleInterface dsInstance = getDsInstance(dsClazz);
                    ArrayList<String> args = new ArrayList<>();
                    metadataSet.forEach(e->{
                        if (isBaseType(e.fieldClass)) {
                            args.add(processBaseType(pjp.getArgs()[e.argIndex]));
                        }else {
                            args.add(processUnBaseType(pjp.getArgs()[e.argIndex],e.dsObjectFieldNames));
                        }
                    });
                    String[] parameterNames = methodSignature.getParameterNames();
                    String dsValue = null;
                    for (int i = 0; i < parameterNames.length; i++) {
                        if (parameterNames[i].equals(dsValueFieldName))
                        {
                            try {
                                dsValue = (String) pjp.getArgs()[i];
                                if (dsValue != null) dsValue = dsValue.toLowerCase();
                            }catch (Exception ex){
                                log.error("ds field must be String");
                                throw new RuntimeException();
                            }
                            break;
                        }
                    }
                    String[] argsArr = args.toArray(new String[0]);
                    AssertUrils.state(ObjectUtils.nullSafeEquals(dsInstance.verify((Object[]) argsArr),dsValue),new RuntimeException());
                }
            }
        }catch (Exception e){
            throw new DynamicVerificationError();
        }
        return pjp.proceed();
    }

    private String processUnBaseType(Object obj,String[] inlineTypeName){
        if(obj == null) return "";
        StringBuilder sb = new StringBuilder();
        if (obj instanceof List<?> list)
        {
            list.forEach(e->{
                sb.append(processUnBaseType(e,inlineTypeName));
            });
            return sb.toString();
        }

        if (obj instanceof Map<?,?> map)
        {
            for (String s : inlineTypeName) {
                sb.append(processBaseType(map.get(s)));
            }
            return sb.toString();
        }

        for (String s : inlineTypeName) {
            try {
                Field field = obj.getClass().getDeclaredField(s);
                boolean b = field.canAccess(obj);
                field.setAccessible(true);
                Object o = field.get(obj);
                field.setAccessible(b);
                sb.append(processBaseType(o));
            }
            catch (NoSuchFieldException | IllegalAccessException ignored) { }
        }
        return sb.toString();
    }

    private String processBaseType(Object obj){
        if (obj == null)
            return "";
        AssertUrils.state(isBaseType(obj.getClass()),new RuntimeException("base type error"));
        if (obj instanceof Date date)
            return String.valueOf(date.getTime());
        return obj.toString();
    }

    private boolean isBaseType(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class
                || clazz == Integer.class || clazz == Byte.class
                || clazz == Long.class || clazz == Double.class
                || clazz == Float.class || clazz == Date.class
                || clazz == Short.class || clazz == Boolean.class || clazz == Void.class;
    }

    private DynamicVerRuleInterface getDsInstance(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (dsInstanceCache.containsKey(clazz.getName())) {
            return dsInstanceCache.get(clazz.getName());
        }
        Constructor<?> constructor = clazz.getConstructor(String.class);
        DynamicVerRuleInterface c = (DynamicVerRuleInterface) constructor.newInstance(commonConfig.getSecurity().getDsKey());
        dsInstanceCache.put(clazz.getName(), c);
        return c;
    }


    private Set<DsFieldsMetadata> getMethodDsFieldsMetadata(Method method) {
        TreeSet<DsFieldsMetadata> resSet = new TreeSet<>();
        String[] methodParamName = getMethodParamName(method);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < methodParamName.length; i++) {
            Annotation[] anns = parameterAnnotations[i];
            Annotation ann = findAnnotation(anns, DsTargetField.class);
            if (ann != null)
            {
                DsFieldsMetadata fm = new DsFieldsMetadata();
                fm.fieldAnnotation = ann;
                fm.fieldName = methodParamName[i];
                fm.fieldClass = parameterTypes[i];
                fm.argIndex = i;
                if (ann instanceof DsTargetField dsTargetField)
                {
                    fm.order = dsTargetField.order();
                    fm.dsObjectFieldNames = dsTargetField.value();
                }else throw new RuntimeException("annotation type error");
                resSet.add(fm);
            }
        }
        return resSet;
    }

    private Annotation findAnnotation(Annotation[] annotations, Class<?> annotationClass) {
        for (Annotation a : annotations) {
            if (a.annotationType().getName().equals(annotationClass.getName())) {
                return a;
            }
        }
        return null;
    }

    private String[] getMethodParamName(Method method)
    {
        String methodSign = method.getDeclaringClass().getName()+method.getName();
        if (methodParamCache.containsKey(methodSign))
        {
            return methodParamCache.get(methodSign);
        }
        DefaultParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
        String[] parameterNames = pnd.getParameterNames(method);
        if (parameterNames == null) {
            parameterNames = new String[0];
        }
        methodParamCache.put(methodSign,parameterNames);
        return parameterNames;
    }


    public static class DsFieldsMetadata implements Comparable<DsFieldsMetadata> {
        public Class<?> fieldClass;

        public Annotation fieldAnnotation;

        public String fieldName;

        public int order;

        public String[] dsObjectFieldNames;

        public int argIndex;

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof DsFieldsMetadata dsFieldsMetadata) {
                return this.fieldName.equals(dsFieldsMetadata.fieldName);
            }
            return false;
        }

        @Override
        public int compareTo(DsFieldsMetadata o) {
            return this.order - o.order == 0 ? 1 : this.order - o.order;
        }
    }

}
