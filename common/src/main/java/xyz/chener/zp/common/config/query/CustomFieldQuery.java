package xyz.chener.zp.common.config.query;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.springframework.util.ReflectionUtils;
import xyz.chener.zp.common.config.query.error.CostomFieldQueryError;
import xyz.chener.zp.common.utils.AssertUrils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @Author: chenzp
 * @Date: 2023/02/08/16:27
 * @Email: chen@chener.xyz
 */
public class CustomFieldQuery {

    private static final ThreadLocal<List<TableField>> localVar = new ThreadLocal<>();

    private static final Map<Class<?>,List<TableField>> mbpModelEntityFieldsCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>,List<TableField>> normalModelEntityFieldsCache = new ConcurrentHashMap<>();

    public static class TableField{
        public String tableName;
        public String fieldName;
    }

    public static class CustomFieldQueryCloseable implements AutoCloseable{
        @Override
        public void close() {
            localVar.remove();
        }
    }

    public static CustomFieldQueryCloseable StartQuery(CustomFieldQuery query,Class<?> entityClass){
        List<String> cols = query.getQueryFields();
        AssertUrils.state(!entityClass.equals(CustomFieldQuery.class),new CostomFieldQueryError("StartQuery entity class cannot be its own"));
        if (entityClass.getSuperclass().equals(Model.class)) {
            List<TableField> mbpModelEntityFields = getMbpModelEntityFields(entityClass);
            List<TableField> tableAndFields = mbpModelEntityFields.stream()
                    .filter(e -> cols.contains(humpDB2E(e.fieldName,false))).toList();
            localVar.set(tableAndFields);
        }else {
            List<TableField> entityFields = getEntityFields(entityClass);
            List<TableField> tableAndFields = entityFields.stream()
                    .filter(e -> cols.contains(humpDB2E(e.fieldName, false))).toList();
            localVar.set(tableAndFields);
        }
        return new CustomFieldQueryCloseable();
    }

    public static <T> LambdaQueryChainWrapper<T> StartQuery(CustomFieldQuery query
            ,LambdaQueryChainWrapper<T> mbpLambdaQuery){
        if (query.getQueryFields().size() == 0)
            return mbpLambdaQuery;
        mbpLambdaQuery.select(tableFieldInfo -> query.getQueryFields().contains(tableFieldInfo.getEl()));
        return mbpLambdaQuery;
    }


    public CustomFieldQueryCloseable StartQuery(Class<?> entityClass) {
        return CustomFieldQuery.StartQuery(this,entityClass);
    }

    public CustomFieldQueryCloseable StartQuery() {
        return CustomFieldQuery.StartQuery(this,this.getClass());
    }


    private static List<TableField> getEntityFields(Class<?> entityClass){
        List<TableField> cache = normalModelEntityFieldsCache.get(entityClass);
        if (cache!=null)
            return cache;
        List<TableField> list = new ArrayList<>();
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(entityClass);
        Arrays.stream(allDeclaredMethods).filter(e-> e.getModifiers() == Modifier.PUBLIC
                && e.getName().indexOf("get") == 0
                && e.getName().length() > 3
                && e.getParameterCount() == 0 && !e.getDeclaringClass().equals(CustomFieldQuery.class)
                && e.getReturnType() != void.class).forEach(e->{
                    String tableName = humpE2DB(entityClass.getSimpleName());
                    String fieldName = humpE2DB(e.getName().substring(3));
                    try {
                        String fn = e.getName().substring(3);
                        fn = fn.substring(0,1).toLowerCase() + fn.substring(1);
                        tableName = e.getDeclaringClass().getDeclaredField(fn).getAnnotation(QueryTableName.class).value();
                    }catch (Exception ignored){}
                    TableField tableField = new TableField();
                    tableField.tableName = tableName;
                    tableField.fieldName = fieldName;
                    list.add(tableField);
        });
        normalModelEntityFieldsCache.put(entityClass,list);
        return list;
    }

    private static List<TableField> getMbpModelEntityFields(Class<?> entityClass){
        List<TableField> cache = mbpModelEntityFieldsCache.get(entityClass);
        if (cache!=null)
            return cache;
        List<TableField> list = new ArrayList<>();
        String tableName = entityClass.getSimpleName();
        TableName tableNameAnn = entityClass.getAnnotation(TableName.class);
        if (tableNameAnn != null)
            tableName = tableNameAnn.value();
        else {
            tableName = humpE2DB(tableName);
        }
        final String finalTableName = tableName;
        Arrays.stream(entityClass.getMethods()).filter(e-> e.getName().startsWith("get")
                && e.getParameterCount() == 0
                && e.getName().length() > 3
                && e.getReturnType() != void.class)
                .forEach(e->{
                    String lstn = finalTableName;
                    String fn = e.getName().substring(3);
                    fn = fn.substring(0,1).toLowerCase() + fn.substring(1);
                    try {
                        lstn = entityClass.getDeclaredField(fn).getAnnotation(QueryTableName.class).value();
                    }catch (Exception ignored){}

                    TableField tableField = new TableField();
            tableField.tableName = lstn;
            tableField.fieldName = humpE2DB(e.getName().substring(3));
            list.add(tableField);
        });
        mbpModelEntityFieldsCache.put(entityClass,list);
        return list;
    }

    private static String humpE2DB(String str){
        str = str.replaceAll("[A-Z]", "_$0").toLowerCase();
        if (str.indexOf("_") == 0)
            str = str.substring(1);
        return str;
    }

    private static String humpDB2E(String str,boolean isTable){
        str = str.toLowerCase();
        String[] split = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s.substring(0,1).toUpperCase()).append(s.substring(1));
        }
        if (isTable)
        {
            return sb.toString();
        }
        return sb.substring(0,1).toLowerCase() + sb.substring(1);
    }




    public static List<TableField> getQuery()
    {
        List<TableField> list = localVar.get();
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        localVar.remove();
        return list;
    }



    private List<String> queryFields = new ArrayList<>();

    public List<String> getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(List<String> queryFields) {
        this.queryFields = queryFields;
    }
}
