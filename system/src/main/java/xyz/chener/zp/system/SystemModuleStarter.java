package xyz.chener.zp.system;

import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/16:35
 * @Email: chen@chener.xyz
 */

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class
        , SentinelEndpointAutoConfiguration.class},
        excludeName = {"org.redisson.spring.starter.RedissonAutoConfiguration"}
)
@EnableFeignClients
public class SystemModuleStarter {
    public static void main(String[] args) {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication app = new SpringApplication(SystemModuleStarter.class);
        app.run(args);
    }
}
