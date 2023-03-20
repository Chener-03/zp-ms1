package xyz.chener.zp.sentinelAdapter.actuator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/16:28
 * @Email: chen@chener.xyz
 */

@Configuration
public class DefauultSentinelCustomActuator {

    @Bean
    @ConditionalOnMissingBean
    public DefaultQpsEndpoint defaultQpsEndpoint(){
        return new DefaultQpsEndpoint();
    }

}
