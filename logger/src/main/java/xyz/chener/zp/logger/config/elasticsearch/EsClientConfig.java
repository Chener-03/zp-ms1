package xyz.chener.zp.logger.config.elasticsearch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.chener.zp.common.config.CommonConfig;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/09:15
 * @Email: chen@chener.xyz
 */

@Configuration
public class EsClientConfig {

    private final CommonConfig commonConfig;


    public EsClientConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }


    @Bean
    public LoggerPush loggerPush(){
        return new LoggerPush(commonConfig);
    }

}
