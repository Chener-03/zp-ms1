package xyz.chener.zp.common.config.query;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.springframework.util.ReflectionUtils;
import xyz.chener.zp.common.config.query.annotation.QueryTableName;
import xyz.chener.zp.common.config.query.entity.ChainParam;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.config.query.entity.ResultWrapper;
import xyz.chener.zp.common.config.query.error.CostomFieldQueryError;
import xyz.chener.zp.common.config.query.processor.MbpNormalChange;
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
        if (cols == null || cols.size() == 0)
            return new CustomFieldQueryCloseable();
        AssertUrils.state(!entityClass.equals(FieldQuery.class),new CostomFieldQueryError("StartQuery entity class cannot be its own"));

        ChainParam param = new ChainParam();
        param.queryFields = cols;
        param.entityClass = entityClass;
        List<TableField> tableAndFields = null;
        try {
            ResultWrapper resultWrapper = (ResultWrapper) ChainStarter.startTree(new MbpNormalChange(), param);
            tableAndFields = resultWrapper.result();
        } catch (Exception ignored) { }
        if (tableAndFields != null) {
            localVar.set(tableAndFields);
        }
        return new CustomFieldQueryCloseable();
    }


    public static <T> LambdaQueryChainWrapper<T> StartQuery(FieldQuery query
            , LambdaQueryChainWrapper<T> mbpLambdaQuery){
        if (query.getQueryFields().size() == 0)
            return mbpLambdaQuery;
        mbpLambdaQuery.select(tableFieldInfo -> query.getQueryFields().contains(tableFieldInfo.getEl()));
        return mbpLambdaQuery;
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
