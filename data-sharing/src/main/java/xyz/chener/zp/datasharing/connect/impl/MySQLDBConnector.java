package xyz.chener.zp.datasharing.connect.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.datasharing.config.DataSharingSourceConfig;
import xyz.chener.zp.datasharing.connect.DBConnector;
import xyz.chener.zp.datasharing.connect.entity.DataSourceStruce;
import xyz.chener.zp.datasharing.connect.error.ConnectError;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLDBConnector implements DBConnector {

    @Override
    public boolean testConnection(String host, String port, String params, String databaseName, String username, String password) throws ConnectError {

        String url = prepareURL(host, port, params, databaseName);
        Connection connection = null;
        try {
            if (StringUtils.hasText(username))
            {
                connection = DriverManager.getConnection(url, username, password);
            }else {
                connection = DriverManager.getConnection(url);
            }
        }catch (Exception exception){
            throw new ConnectError(exception.getMessage(),exception);
        }
        try {
            connection.close();
        } catch (Exception ignored) { }
        return true;
    }

    @Override
    public DataSourceStruce getDataSource(String host, String port, String params, String databaseName, String username, String password) throws ConnectError {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(prepareURL(host, port, params, databaseName));
        if (StringUtils.hasText(username)) {
            cfg.setUsername(username);
            cfg.setPassword(password);
        }
        DataSharingSourceConfig sharingSourceConfig = ApplicationContextHolder.getApplicationContext().getBean(DataSharingSourceConfig.class);
        cfg.setMaximumPoolSize(sharingSourceConfig.getMaxPoolSize());
        cfg.setConnectionTimeout(sharingSourceConfig.getConnectionTimeout());
        cfg.setMaxLifetime(sharingSourceConfig.getMaxLifeTime());

        HikariDataSource hikariDataSource = new HikariDataSource(cfg);

        DataSourceStruce res = new DataSourceStruce();
        res.setDataSource(hikariDataSource);
        res.setDatasourceClass(hikariDataSource.getClass());
        try {
            res.setDriverClass(Class.forName(hikariDataSource.getDriverClassName()));
        } catch ( Exception ignored) { }
        return res;
    }

    private static String prepareURL(String host, String port, String params, String databaseName) {
        String url = String.format("jdbc:mysql://%s:%s/%s", host, port, databaseName);
        if (StringUtils.hasText(params)) {
            url += "?" + params;
        }
        return url;
    }
}
