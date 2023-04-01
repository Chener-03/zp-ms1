package xyz.chener.zp.datasharing.connect.impl;

import org.springframework.util.StringUtils;
import xyz.chener.zp.datasharing.connect.DBConnector;
import xyz.chener.zp.datasharing.connect.error.ConnectError;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    private static String  prepareURL(String host, String port, String params, String databaseName) {
        String url = String.format("jdbc:mysql://%s:%s/%s", host, port, databaseName);
        if (StringUtils.hasText(params)) {
            url += "?" + params;
        }
        return url;
    }
}
