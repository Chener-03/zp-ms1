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
 * @Date: 2023/02/08/16:27
 * @Email: chen@chener.xyz
 */
public class FieldQuery {

    private List<String> queryFields = new ArrayList<>();

    public List<String> getQueryFields() {
        if (queryFields == null)
            return new ArrayList<>();
        return queryFields;
    }

    public void setQueryFields(List<String> queryFields) {
        this.queryFields = queryFields;
    }

    public QueryHelper.CustomFieldQueryCloseable startQuery(Class<?> entityClass){
        return QueryHelper.StartQuery(this,entityClass);
    }
    public QueryHelper.CustomFieldQueryCloseable startQuery( ){
        return QueryHelper.StartQuery(this,this.getClass());
    }
}
