package xyz.chener.zp.common.config.mbp;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import xyz.chener.zp.common.utils.CustomFieldQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: chenzp
 * @Date: 2023/02/08/17:02
 * @Email: chen@chener.xyz
 */

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
@Slf4j
public class CustomFieldsQueryInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        BoundSql boundSql;
        if (args.length == 4) {
            boundSql = ms.getBoundSql(parameter);
        } else {
            boundSql = (BoundSql) args[5];
        }
        List<String> query = CustomFieldQuery.getQuery();
        if (query.size() == 0)
        {
            return invocation.proceed();
        }
        String oldSql = null;
        try {
            oldSql = processBoundSql(boundSql,query);
            return invocation.proceed();
        }catch (Throwable e){
            log.warn("Parameter restriction error, the original sql query content will be returned!");
            if (oldSql != null)
                resetBoundSql(oldSql,boundSql);
            return invocation.proceed();
        }
    }

    private String processBoundSql(BoundSql boundSql,List<String> query)
    {
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        String oldSql = mpBoundSql.sql();
        String newSql = null;
        if (isCountSql(oldSql)) {
            CustomFieldQuery.StartQuery(query);
            return oldSql;
        }


        return oldSql;
    }

    private String replaceSqlSelect(List<String> list)
    {

    }

    private boolean isCountSql(String sql)
    {
        return sql.matches("(?i)^\\s*select\\s+count\\s*\\(.*\\)\\s+.*");
    }

    private List<String> getSqlSelectFields(String sql)
    {
        int select = sql.toLowerCase().indexOf("select");
        if (select == -1)
        {
            log.warn("The sql statement maybe not a select statement!");
            throw new RuntimeException();
        }
        int from = sql.toLowerCase().indexOf("from");
        if (from == -1)
        {
            log.warn("The Sql may not contain from!");
            throw new RuntimeException();
        }
        List<String> fields = new ArrayList<>();
        Pattern pattern = Pattern.compile("SELECT\\s+([^,]+),");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            fields.add(matcher.group(1));
        }
    }

    private void resetBoundSql(String sql,BoundSql boundSql)
    {
        PluginUtils.mpBoundSql(boundSql).sql(sql);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
