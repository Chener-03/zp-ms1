package xyz.chener.zp.workflow;

import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.chener.zp.workflow.utils.Json;

/**
 * @Author: chenzp
 * @Date: 2023/06/19/09:43
 * @Email: chen@chener.xyz
 */


@SpringBootApplication(excludeName = {"org.redisson.spring.starter.RedissonAutoConfiguration"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
public class WorkFlowApplication {
    public static void main(String[] args) throws  Exception {
        System.setProperty("csp.sentinel.log.output.type","console");
        System.setProperty("polyglot.engine.WarnInterpreterOnly","false");
        org.springframework.boot.SpringApplication.run(WorkFlowApplication.class, args);
    }
}
