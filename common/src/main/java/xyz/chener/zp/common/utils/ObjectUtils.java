package xyz.chener.zp.common.utils;

import xyz.chener.zp.common.entity.SFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

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

}
