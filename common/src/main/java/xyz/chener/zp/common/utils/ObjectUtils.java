package xyz.chener.zp.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import xyz.chener.zp.common.entity.SFunction;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author: chenzp
 * @Date: 2023/02/02/13:01
 * @Email: chen@chener.xyz
 */
public class ObjectUtils extends org.springframework.util.ObjectUtils {


    /**
     * 比较两个对象的字段是否相等
     * @param o1
     * @param o2
     * @param fields
     * @return
     * @param <T>
     */
    @SafeVarargs
    public static <T> boolean objectFieldsEquals(T o1, T o2, SFunction<T, ?> ... fields)
    {
        if(o1 == null || o2 == null) return o2==o1;

        if (!o1.getClass().equals(o2.getClass()))
            return false;

        if (fields.length == 0)
        {
            for (Method mf : Arrays.stream(o1.getClass().getDeclaredMethods())
                    .filter(e -> e.getName().indexOf("get") == 0 && e.getModifiers() == Modifier.PUBLIC)
                    .toList())
            {
                try {
                    if (!nullSafeEquals(mf.invoke(o1),mf.invoke(o2))) {
                        return false;
                    }
                }catch (Exception e){
                    return false;
                }
            }
        }else
        {
            for (SFunction<T, ?> e : fields) {
                String funName = getSFunctionName(e);
                if (funName != null)
                {
                    try {
                        Method mf = o1.getClass().getDeclaredMethod(funName);
                        if (!nullSafeEquals(mf.invoke(o1),mf.invoke(o2))) {
                            return false;
                        }
                    }catch (Exception exception)
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * 获取Function的方法名
     * @param e
     * @param <T>
     * @return
     */
    public static <T> String getSFunctionName(SFunction<T,?> e)
    {
        try {
            Method writeReplaceMethod = e.getClass().getDeclaredMethod("writeReplace");
            writeReplaceMethod.setAccessible(true);
            java.lang.invoke.SerializedLambda serializedLambda;
            serializedLambda = (java.lang.invoke.SerializedLambda) writeReplaceMethod.invoke(e);
            writeReplaceMethod.setAccessible(false);
            return serializedLambda.getImplMethodName();
        }catch (Exception exception)
        {
            xyz.chener.zp.common.entity.SerializedLambda extract = xyz.chener.zp.common.entity.SerializedLambda.extract(e);
            return extract.getImplMethodName();
        }
    }


    /**
     * 拷贝对象的字段
     * @param source
     * @param target
     */
    public static void copyFields(Object source, Object target)
    {
        if(source == null || target == null) return;
        Class<?> clz2 = target.getClass();
        Arrays.stream(source.getClass().getDeclaredFields()).forEach(e->{
            try {
                Field f = clz2.getDeclaredField(e.getName());
                boolean targetAccess = f.canAccess(target);
                boolean sourceAccess = e.canAccess(source);
                f.setAccessible(true);
                e.setAccessible(true);
                f.set(target,e.get(source));
                f.setAccessible(targetAccess);
                e.setAccessible(sourceAccess);
            }catch (Exception exception){}
        });
    }


    public static Boolean isBasicType(Object obj) {
        if (obj instanceof String) {
            return true;
        } else if (obj instanceof Integer) {
            return true;
        } else if (obj instanceof Long) {
            return true;
        } else if (obj instanceof Double) {
            return true;
        } else if (obj instanceof Float) {
            return true;
        } else if (obj instanceof BigDecimal) {
            return true;
        }else if (obj instanceof BigInteger) {
            return true;
        } else if (obj instanceof Boolean) {
            return true;
        } else if (obj instanceof Byte) {
            return true;
        } else if (obj instanceof Character) {
            return true;
        } else if (obj instanceof Short) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isBasicType(Class<?> clz) {
        if (clz == String.class) {
            return true;
        } else if (clz == Integer.class) {
            return true;
        } else if (clz == Long.class) {
            return true;
        } else if (clz == Double.class) {
            return true;
        } else if (clz == Float.class) {
            return true;
        } else if (clz == BigDecimal.class) {
            return true;
        }else if (clz == BigInteger.class) {
            return true;
        } else if (clz == Boolean.class) {
            return true;
        } else if (clz == Byte.class) {
            return true;
        } else if (clz == Character.class) {
            return true;
        } else if (clz == Short.class) {
            return true;
        } else {
            return false;
        }
    }


    public static class EntityChainWrapper<T>{
        public EntityChainWrapper<T> set(SFunction<T,?> sf, Object a)
        {
            String funName = getSFunctionName(sf);
            if (funName.startsWith("get")) {
                funName = "set" + funName.substring(3);
            }
            try{
                Method method = clz.getMethod(funName,a.getClass());
                method.invoke(entity,a);
            }catch (Exception exception){
                throw new RuntimeException(exception);
            }
            return this;
        }

        private EntityChainWrapper(){}

        public T build()
        {
            return entity;
        }

        private T entity;

        private Class<T> clz;

        public static <T> EntityChainWrapper<T> builder(Class<T> clz){
            EntityChainWrapper<T> r = null;
            r = new EntityChainWrapper<T>();
            r.clz = clz;
            try {
                r.entity = clz.getConstructor().newInstance();
            } catch ( Exception e) {
                throw new RuntimeException(e);
            }
            return r;
        }

    }



    public static long getSerializableObjectSize(Object obj){
        int length = 0;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            length = baos.toByteArray().length;
            oos.close();
            baos.reset();
            baos.close();
        }catch (Throwable ignored){ }
        return length;
    }

    public static Object newInstance(Class<?> clazz){
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T newInstance(String classFullName){
        try {
            return (T) newInstance(Class.forName(classFullName));
        } catch (Exception e) {
            return null;
        }
    }


}
