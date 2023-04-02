package xyz.chener.zp.datasharing.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zp.datasharing.datasource")
public class DataSharingSourceConfig {

    private Integer maxPoolSize = 10;

    private Integer connectionTimeout = 5000;


    private Integer maxLifeTime = 60000;

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getMaxLifeTime() {
        return maxLifeTime;
    }

    public void setMaxLifeTime(Integer maxLifeTime) {
        this.maxLifeTime = maxLifeTime;
    }
}
