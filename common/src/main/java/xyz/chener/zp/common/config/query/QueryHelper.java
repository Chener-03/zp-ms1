package xyz.chener.zp.common.config.query;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.springframework.util.ReflectionUtils;
import xyz.chener.zp.common.config.query.error.CostomFieldQueryError;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.chain.AbstractChainTreeExecute;
import xyz.chener.zp.common.utils.chain.ChainStarter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chenzp
 * @Date: 2023/03/01/10:10
 * @Email: chen@chener.xyz
 */
public class QueryHelper {

    private static final ThreadLocal<List<TableField>> localVar = new ThreadLocal<>();


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

    /**
     *
     * @param query         自定义查询字段
     * @param entityClass   目标实体类Class
     * @return
     */
    public static CustomFieldQueryCloseable StartQuery(FieldQuery query, Class<?> entityClass){
        List<String> cols = query.getQueryFields();
        AssertUrils.state(!entityClass.equals(FieldQuery.class),new CostomFieldQueryError("StartQuery entity class cannot be its own"));

        ChainParam param = new ChainParam();
        param.queryFields = cols;
        param.entityClass = entityClass;
        List<TableField> tableAndFields = null;
        try {
            tableAndFields = (List<TableField>) ChainStarter.startTree(new MbpNormalChange(),param);
        } catch (Exception ignored) { }
        if (tableAndFields != null) {
            localVar.set(tableAndFields);
        }
        return new CustomFieldQueryCloseable();
    }

    private static class ChainParam{
        public List<String> queryFields;
        public Class<?> entityClass;
    }

    private static class MbpNormalChange extends AbstractChainTreeExecute {
        @Override
        protected AbstractChainTreeExecute changeNext(Object param) {
            if (param instanceof ChainParam p){
                if (p.entityClass.getSuperclass().equals(Model.class)) {
                    return new MbpProcess();
                }
                return new NormalProcess();
            }
            return null;
        }

        @Override
        protected Object handle(Object param) throws Exception {
            return param;
        }
    }

    private static class MbpProcess extends AbstractChainTreeExecute{
        private static final Map<Class<?>,List<TableField>> mbpModelEntityFieldsCache = new ConcurrentHashMap<>();

        @Override
        protected Object handle(Object param) throws Exception {
            if (param instanceof ChainParam p)
            {
                return getMbpModelEntityFields(p.entityClass).stream()
                        .filter(e -> p.queryFields.contains(humpDB2E(e.fieldName,false))).toList();
            }
            return null;
        }
        private List<TableField> getMbpModelEntityFields(Class<?> entityClass){
            List<TableField> cache = mbpModelEntityFieldsCache.get(entityClass);
            if (cache!=null)
                return cache;
            List<TableField> list = new ArrayList<>();
            String tableName = entityClass.getSimpleName();
            TableName tableNameAnn = entityClass.getAnnotation(TableName.class);
            if (tableNameAnn != null)
                tableName = tableNameAnn.schema() + tableNameAnn.value();
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
    }

    private static class NormalProcess extends AbstractChainTreeExecute{
        private static final Map<Class<?>,List<TableField>> normalModelEntityFieldsCache = new ConcurrentHashMap<>();

        @Override
        protected Object handle(Object param) throws Exception {
            if (param instanceof ChainParam p)
            {
                return getEntityFields(p.entityClass).stream()
                        .filter(e -> p.queryFields.contains(humpDB2E(e.fieldName, false))).toList();
            }
            return null;
        }
        private List<TableField> getEntityFields(Class<?> entityClass){
            List<TableField> cache = normalModelEntityFieldsCache.get(entityClass);
            if (cache!=null)
                return cache;
            List<TableField> list = new ArrayList<>();
            Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(entityClass);
            Arrays.stream(allDeclaredMethods).filter(e-> e.getModifiers() == Modifier.PUBLIC
                    && e.getName().indexOf("get") == 0
                    && e.getName().length() > 3
                    && e.getParameterCount() == 0
                    && !e.getDeclaringClass().equals(FieldQuery.class)
                    && e.getReturnType() != void.class).forEach(e->{
                String tableName = humpE2DB(entityClass.getSimpleName());
                TableName mbpTableNameAnn = e.getDeclaringClass().getAnnotation(TableName.class);
                if (mbpTableNameAnn!=null) {
                    tableName = mbpTableNameAnn.schema() + mbpTableNameAnn.value();
                }
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
    }

    public static <T> LambdaQueryChainWrapper<T> StartQuery(FieldQuery query
            , LambdaQueryChainWrapper<T> mbpLambdaQuery){
        if (query.getQueryFields().size() == 0)
            return mbpLambdaQuery;
        mbpLambdaQuery.select(tableFieldInfo -> query.getQueryFields().contains(tableFieldInfo.getEl()));
        return mbpLambdaQuery;
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




    static List<TableField> getQuery()
    {
        List<TableField> list = localVar.get();
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        localVar.remove();
        return list;
    }


}
