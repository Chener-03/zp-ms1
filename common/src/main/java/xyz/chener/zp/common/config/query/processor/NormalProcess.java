package xyz.chener.zp.common.config.query.processor;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.util.ReflectionUtils;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.config.query.annotation.QueryTableName;
import xyz.chener.zp.common.config.query.entity.ChainParam;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.config.query.entity.ResultWrapper;
import xyz.chener.zp.common.utils.chain.AbstractChainTreeExecute;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chenzp
 * @Date: 2023/03/09/14:33
 * @Email: chen@chener.xyz
 */
public class NormalProcess extends AbstractChainTreeExecute {
    private static final Map<Class<?>, List<QueryHelper.TableField>> normalModelEntityFieldsCache = new ConcurrentHashMap<>();

    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof ChainParam p)
        {
            List<QueryHelper.TableField> tableFields = getEntityFields(p.entityClass).stream()
                    .filter(e -> p.queryFields.contains(humpDB2E(e.fieldName, false))).toList();
            return new ResultWrapper(p,tableFields);
        }
        return null;
    }

    @Override
    protected AbstractChainTreeExecute changeNext(Object param) {
        return new MustFieldProcess();
    }

    private List<QueryHelper.TableField> getEntityFields(Class<?> entityClass){
        List<QueryHelper.TableField> cache = normalModelEntityFieldsCache.get(entityClass);
        if (cache!=null)
            return cache;
        List<QueryHelper.TableField> list = new ArrayList<>();
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
            QueryHelper.TableField tableField = new QueryHelper.TableField();
            tableField.tableName = tableName;
            tableField.fieldName = fieldName;
            list.add(tableField);
        });
        normalModelEntityFieldsCache.put(entityClass,list);
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

}