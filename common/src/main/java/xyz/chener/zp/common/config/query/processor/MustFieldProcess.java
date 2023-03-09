package xyz.chener.zp.common.config.query.processor;

import com.baomidou.mybatisplus.annotation.TableName;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.config.query.annotation.QueryMustField;
import xyz.chener.zp.common.config.query.annotation.QueryTableName;
import xyz.chener.zp.common.config.query.entity.ChainParam;
import xyz.chener.zp.common.config.query.entity.ResultWrapper;
import xyz.chener.zp.common.utils.chain.AbstractChainTreeExecute;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/09/14:53
 * @Email: chen@chener.xyz
 */

public class MustFieldProcess extends AbstractChainTreeExecute {
    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof ResultWrapper p){
            ChainParam cp = p.param();
            List<QueryHelper.TableField> res = p.result();
            if (res == null || res.size() == 0)
                return new ResultWrapper(cp,res);
            ArrayList<QueryHelper.TableField> tableFields = new ArrayList<>(res);
            Arrays.stream(cp.entityClass.getDeclaredFields())
                    .filter(e->e.getAnnotation(QueryMustField.class)!=null)
                    .forEach(e->{
                        String fieldName = humpE2DB(e.getName());
                        String tableName = getFieldTableName(e);
                        QueryHelper.TableField tf = new QueryHelper.TableField();
                        tf.fieldName = fieldName;
                        tf.tableName = tableName;
                        tableFields.add(tf);
                    });
            return new ResultWrapper(cp,tableFields);
        }
        return null;
    }

    private String getFieldTableName(Field field) {
        Class<?> clz = field.getDeclaringClass();
        String tableName = humpE2DB(clz.getSimpleName());
        TableName ann1 = clz.getAnnotation(TableName.class);
        if (ann1 != null)
            tableName = ann1.schema() + ann1.value();
        QueryTableName ann2 = field.getAnnotation(QueryTableName.class);
        if (ann2 != null)
            tableName = ann2.value();
        return tableName;
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
