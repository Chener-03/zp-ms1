package xyz.chener.zp.common.config.query;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.List;

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
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        if (args.length == 4) {
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }
        List<QueryHelper.TableField> query = QueryHelper.getQuery();

        if (query.size() == 0)
        {
            return invocation.proceed();
        }
        String oldSql = null;
        try {
            oldSql = processBoundSql(boundSql,query);
            return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        }catch (Throwable e){
            log.warn("Parameter restriction error, the original sql query content will be returned!");
            if (oldSql != null)
                resetBoundSql(oldSql,boundSql);
            return invocation.proceed();
        }
    }

    private String processBoundSql(BoundSql boundSql,List<QueryHelper.TableField> query) throws Exception
    {
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        String oldSql = mpBoundSql.sql();

        Select select = (Select) CCJSqlParserUtil.parse(oldSql);
        PlainSelect selectBody = (PlainSelect) select.getSelectBody();
        ArrayList<Table> allTable = new ArrayList<>();
        //ArrayList<SelectExpressionItem> allSelect = new ArrayList<>();
        selectBody.getFromItem().accept(new FromItemVisitorAdapter() {
            @Override
            public void visit(Table table) {
                allTable.add(table);
            }
        });
        if (selectBody.getJoins()!=null)
        {
            selectBody.getJoins().forEach(it->{
                it.getRightItem().accept(new FromItemVisitorAdapter() {
                    @Override
                    public void visit(Table table) {
                        allTable.add(table);
                    }
                });
            });
        }
        /*        selectBody.getSelectItems().forEach(it->{
            it.accept(new SelectItemVisitorAdapter(){
                @Override
                public void visit(SelectExpressionItem selectExpressionItem) {
                    allSelect.add(selectExpressionItem);
                }
            });
        });*/

        List<QueryHelper.TableField> allowedFields = query.stream()
                .filter(e -> containsTable(allTable, e.tableName)).toList();
        List<SelectItem> selectItems = buildSelectItem(allowedFields, allTable);
        if (selectItems.size() > 0)
        {
            selectBody.setSelectItems(selectItems);
            resetBoundSql(selectBody.toString(),boundSql);
        }
        return oldSql;
    }

    private boolean containsTable(List<Table> tables,String tableName)
    {
        return tables.stream().anyMatch(it->it.getName().equals(tableName));
    }

    private Table findTable(List<Table> tables,String tableName)
    {
        return tables.stream().filter(it->it.getName().equals(tableName)).findFirst().orElse(null);
    }

    private List<SelectItem> buildSelectItem(List<QueryHelper.TableField> fields
            ,List<Table> allTable)
    {
        List<SelectItem> item = new ArrayList<>();
        fields.forEach(it->{
            Table table = findTable(allTable, it.tableName);
            if (table != null)
            {
                SelectExpressionItem si = new SelectExpressionItem();
                si.setExpression(buildColumn(table, it.fieldName));
                item.add(si);
            }
        });
        return item;
    }

    private Column buildColumn(Table table,String fieldName)
    {
        if (table.getAlias() == null) {
            return new Column(fieldName);
        }else {
            return new Column(new Table(table.getAlias().getName()),fieldName);
        }
    }

    private void resetBoundSql(String sql,BoundSql boundSql)
    {
        PluginUtils.mpBoundSql(boundSql).sql(sql);
    }
    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }
}
