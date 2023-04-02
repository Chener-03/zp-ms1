package xyz.chener.zp.datasharing.connect.entity;

import javax.sql.DataSource;

public class DataSourceStruce {

    private DataSource dataSource;

    private Class<?> driverClass;
    private Class<?> datasourceClass;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Class<?> getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(Class<?> driverClass) {
        this.driverClass = driverClass;
    }

    public Class<?> getDatasourceClass() {
        return datasourceClass;
    }

    public void setDatasourceClass(Class<?> datasourceClass) {
        this.datasourceClass = datasourceClass;
    }
}
