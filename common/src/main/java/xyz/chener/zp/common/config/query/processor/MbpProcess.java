package xyz.chener.zp.common.config.query.processor;

import com.baomidou.mybatisplus.annotation.TableName;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.config.query.annotation.QueryTableName;
import xyz.chener.zp.common.config.query.entity.ChainParam;
import xyz.chener.zp.common.config.query.entity.ResultWrapper;
import xyz.chener.zp.common.utils.chain.AbstractChainTreeExecute;

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
public class MbpProcess extends AbstractChainTreeExecute {
    private static final Map<Class<?>, List<QueryHelper.TableField>> mbpModelEntityFieldsCache = new ConcurrentHashMap<>();

    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof ChainParam p)
        {
            List<QueryHelper.TableField> tableFields = getMbpModelEntityFields(p.entityClass).stream()
                    .filter(e -> p.queryFields.contains(humpDB2E(e.fieldName, false))).toList();
            return new ResultWrapper(p,tableFields);
        }
        return null;
    }

    @Override
    protected AbstractChainTreeExecute changeNext(Object param) {
        return new MustFieldProcess();
    }

    private List<QueryHelper.TableField> getMbpModelEntityFields(Class<?> entityClass){
        List<QueryHelper.TableField> cache = mbpModelEntityFieldsCache.get(entityClass);
        if (cache!=null)
            return cache;
        List<QueryHelper.TableField> list = new ArrayList<>();
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

                    QueryHelper.TableField tableField = new QueryHelper.TableField();
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

}
