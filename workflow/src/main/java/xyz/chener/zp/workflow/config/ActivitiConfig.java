package xyz.chener.zp.workflow.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chenzp
 * @Date: 2023/06/19/09:49
 * @Email: chen@chener.xyz
 */

@Configuration
public class ActivitiConfig {

    private final ActivitiProperties activitiProperties;

    public ActivitiConfig(ActivitiProperties activitiProperties) {
        this.activitiProperties = activitiProperties;
    }


    @Bean
    public ProcessEngineConfiguration processEngineConfiguration(){
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngineConfiguration.setDataSource(new HikariDataSource(activitiProperties.getDatasource()));
        return processEngineConfiguration;
    }

    @Bean
    public ProcessEngine processEngine() {
        return processEngineConfiguration().buildProcessEngine();
    }

}


