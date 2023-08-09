package xyz.chener.zp.common.config.dynamicVerification.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.dynamicVerification.annotation.Ds;
import xyz.chener.zp.common.config.dynamicVerification.annotation.DsEntityField;
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
                        Object argObject = pjp.getArgs()[e.argIndex];
                        if (ObjectUtils.isBasicType(e.fieldClass)) {
                            args.add(processBaseType(argObject));
                        }else if (argObject instanceof Date){
                            args.add(processBaseType(argObject));
                        }else if(argObject instanceof Collection list){
                            list.forEach(ls->{
                                processObjectType(ls,e,args);
                            });
                        }else {
                            processObjectType(argObject,e,args);
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
                                throw new RuntimeException("ds field must be String");
                            }
                            break;
                        }
                    }
                    String[] argsArr = args.toArray(new String[0]);
                    AssertUrils.state(ObjectUtils.nullSafeEquals(dsInstance.verify((Object[]) argsArr),dsValue),new RuntimeException("MD5 verification failed"));
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            throw new DynamicVerificationError();
        }
        return pjp.proceed();
    }

    private void processObjectType(Object obj,DsFieldsMetadata metadata,List<String> resList){
        processObjectType(obj,metadata,resList,false);
    }
    private void processObjectType(Object obj,DsFieldsMetadata metadata,List<String> resList,Boolean isObjectRecurrence)
    {
        if (obj instanceof Map && !isObjectRecurrence){
            if (metadata.dsObjectFieldNames.length == 0) {
                log.warn("The target field of dynamic validation is of map type, but the verified field is not marked");
            }else {
                for (String key : metadata.dsObjectFieldNames) {
                    Object o = getObjectWithMultistageKey(obj, key);
                    if (ObjectUtils.isBasicType(o))
                    {
                        resList.add(processBaseType(o));
                    }else {
                        log.warn("The target field in the map must be a certain data type");
                    }
                }
            }
        }else {
            ArrayList<String> objTypeString = new ArrayList<>();
            if (metadata.dsObjectFieldNames.length > 0){
                for (String key : metadata.dsObjectFieldNames) {
                    Object o = getObjectWithMultistageKey(obj, key);
                    if (ObjectUtils.isBasicType(o))
                    {
                        objTypeString.add(processBaseType(o));
                    } else if (o instanceof Date){
                        objTypeString.add(processBaseType(o));
                    }else {
                        log.warn("The target field in the map must be a certain data type");
                    }
                }
            }else {
                Field[] declaredFields = obj.getClass().getDeclaredFields();
                Arrays.stream(declaredFields)
                        .filter(e-> e.getAnnotation(DsEntityField.class)!=null)
                        .sorted((e,p)->{
                            DsEntityField dsEntityField = e.getAnnotation(DsEntityField.class);
                            DsEntityField dsEntityField1 = p.getAnnotation(DsEntityField.class);
                            return dsEntityField.order() - dsEntityField1.order();
                        })
                        .forEach(e->{
                            if (ObjectUtils.isBasicType(e.getType())) {
                                objTypeString.add(processBaseType(getFieldObject(e,obj)));
                            } else if (e.getType().equals(Date.class)){
                                objTypeString.add(processBaseType(getFieldObject(e,obj)));
                            }else {
                                processObjectType(getFieldObject(e,obj),metadata,objTypeString,true);
                            }
                        });
            }

            if (objTypeString.size()>0){
                resList.addAll(objTypeString);
            }else {
                log.warn("The target field of dynamic validation is of object type, but the verified field is not marked or the entity class is not annotated @DsEntityField");
            }
        }
    }

    private Object getFieldObject(Field field,Object obj){
        boolean access = field.canAccess(obj);
        field.setAccessible(true);
        Object o = null;
        try {
            o = field.get(obj);
        } catch (IllegalAccessException ignored) { }
        field.setAccessible(access);
        return o;
    }

    private Object getObjectWithMultistageKey(Object map,String key){
        if (key.contains(".")){
            String[] split = key.split("\\.");
            for (int i = 0; i < split.length; i++) {
                if (i == split.length - 1)
                    return getObjectWithKey(map,split[i]);
                map = getObjectWithKey(map,split[i]);
            }
        }
        return getObjectWithKey(map,key);
    }

    private Object getObjectWithKey(Object map,String key){
        if (map == null) return null;
        if (map instanceof Map){
            return ((Map<?,?>) map).get(key);
        }
        try {
            Field field = map.getClass().getDeclaredField(key);
            boolean access = field.canAccess(map);
            field.setAccessible(true);
            Object o = field.get(map);
            field.setAccessible(access);
            return o;
        } catch (Exception ignored) { }
        return null;
    }

    private String processBaseType(Object obj){
        if (obj == null)
            return "";
        if (obj instanceof Date date)
            return String.valueOf(date.getTime());
        return obj.toString();
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
                DsFieldsMetadata fieldsMetadata = new DsFieldsMetadata();
                fieldsMetadata.fieldAnnotation = ann;
                fieldsMetadata.fieldName = methodParamName[i];
                fieldsMetadata.fieldClass = parameterTypes[i];
                fieldsMetadata.argIndex = i;
                if (ann instanceof DsTargetField dsTargetField)
                {
                    fieldsMetadata.order = dsTargetField.order();
                    fieldsMetadata.dsObjectFieldNames = dsTargetField.value();
                }else throw new RuntimeException("Annotation type error!");
                resSet.add(fieldsMetadata);
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
