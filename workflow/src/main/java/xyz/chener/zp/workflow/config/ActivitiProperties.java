package xyz.chener.zp.workflow.config;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: chenzp
 * @Date: 2023/06/19/09:46
 * @Email: chen@chener.xyz
 */

@ConfigurationProperties(prefix = "zp.workflow.activiti")
public class ActivitiProperties {

    private HikariConfig datasource;


    public HikariConfig getDatasource() {
        return datasource;
    }

    public void setDatasource(HikariConfig datasource) {
        this.datasource = datasource;
    }
}
