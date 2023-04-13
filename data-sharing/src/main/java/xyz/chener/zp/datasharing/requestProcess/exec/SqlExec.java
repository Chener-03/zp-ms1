package xyz.chener.zp.datasharing.requestProcess.exec;

import com.googlecode.aviator.AviatorEvaluator;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.utils.MapBuilder;
import xyz.chener.zp.common.utils.ThreadUtils;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.datasharing.connect.DBConnectorManager;
import xyz.chener.zp.datasharing.connect.entity.DataSourceStruce;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.PeAllParams;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.SqlPe;
import xyz.chener.zp.datasharing.requestProcess.error.SqlTypeNotSupportProcess;
import xyz.chener.zp.datasharing.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlExec extends AbstractChainExecute {

    public static final String UPDATE_COLUMN_NAME = "update_count";

    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof PeAllParams pap){
            SqlPe sqlPe = pap.getSqlPe();
            Integer dataSourceId = sqlPe.getDataSourceId();
            DBConnectorManager connectorManager = ApplicationContextHolder.getApplicationContext().getBean(DBConnectorManager.class);
            DataSourceStruce dataSource = connectorManager.getDataSource(dataSourceId);
            if (dataSource == null) {
                throw new Exception("数据源未配置");
            }

            List<SqlPe.SQL_ENTITY> sqls = sqlPe.getSqls();

            try(Connection connection = dataSource.getDataSource().getConnection()) {
                AtomicInteger count = new AtomicInteger(0);
                for (SqlPe.SQL_ENTITY sqlEntity : sqls) {
                    String sql = sqlEntity.getSql();
                    sql = parseSQL(sql, pap);
                    sqlEntity.setCompileSql(sql);
                    try{
                        if (SqlUtils.getSqlType(sql).equalsIgnoreCase(SqlPe.TYPE_SELECT)) {
                            sqlEntity.setType(SqlPe.TYPE_SELECT);
                        }else {
                            sqlEntity.setType(SqlPe.TYPE_UPDATE);
                        }
                    }catch (Exception e){
                        SqlTypeNotSupportProcess.process(pap.getRequest(),pap.getResponse(),sql);
                        return null;
                    }

                    if (sqlEntity.getType().equalsIgnoreCase(SqlPe.TYPE_SELECT)) {
                        List<Map<String,Object>> resList = processSelect(connection, sql);
                        int ac = count.getAndIncrement();
                        pap.getResult().put("res"+ac,resList);
                    }

                    if (sqlEntity.getType().equalsIgnoreCase(SqlPe.TYPE_UPDATE)) {
                        List<Map<String,Object>> resList = processUpdate(connection, sql);
                        int ac = count.getAndIncrement();
                        pap.getResult().put("res"+ac,resList);
                    }
                }
            }

            return pap;
        }
        return null;
    }

    private List<String> extractVariables(String input) {
        List<String> variables = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String variable = matcher.group(1);
            variables.add(variable);
        }
        return variables;
    }

    private String replaceVariable(String input, List<String> variables, int index, String newValue) {
        StringBuilder sb = new StringBuilder(input);
        int startIndex = input.indexOf("${" + variables.get(index) + "}");
        int endIndex = startIndex + variables.get(index).length() + 3;
        sb.replace(startIndex, endIndex, newValue);
        return sb.toString();
    }


    public String parseSQL(String sql, PeAllParams pap){
        List<String> expressions = extractVariables(sql);
        Map<String, Object> env = new HashMap<>(pap.getNormalParams());
        for (int i = 0; i < expressions.size(); i++) {
            Object res = AviatorEvaluator.execute(expressions.get(i), env);
            sql = replaceVariable(sql, expressions, i, res.toString());
        }
        return sql;
    }


    private List<Map<String,Object>> processSelect(Connection connection,String sql) throws SQLException {
        if (!StringUtils.hasText(sql))
            return Collections.emptyList();
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            List<Map<String,Object>> resList = new ArrayList<>();
            ArrayList<String> columNames = new ArrayList<>();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                columNames.add(columnName);
            }

            while (resultSet.next()){
                Map<String,Object> map = new HashMap<>();
                columNames.forEach(e->{
                    ThreadUtils.runIgnoreException(()-> map.put(e,resultSet.getObject(e)));
                });
                resList.add(map);
            }
            return resList;
        }
    }



    private List<Map<String,Object>> processUpdate(Connection connection,String sql) throws SQLException {
        if (!StringUtils.hasText(sql))
            return Collections.emptyList();
        try(Statement statement = connection.createStatement()) {
            int res = statement.executeUpdate(sql);
            List<Map<String,Object>> resList = new ArrayList<>();
            resList.add(MapBuilder.<String,Object>getInstance().add(UPDATE_COLUMN_NAME,res).build());
            return resList;
        }
    }

}
