package xyz.chener.zp.datasharing.connect;

import xyz.chener.zp.datasharing.connect.error.ConnectError;
import xyz.chener.zp.datasharing.connect.impl.MySQLDBConnector;
import xyz.chener.zp.datasharing.entity.DataSourceType;

public interface DBConnector {

    static DBConnector chooseConnector(String dbType) {
        switch (dbType) {
            case DataSourceType.MYSQL:
                return new MySQLDBConnector();
        }
        return null;
    }


    boolean testConnection(String host, String port, String params,String databaseName
            , String username, String password) throws ConnectError;



}
