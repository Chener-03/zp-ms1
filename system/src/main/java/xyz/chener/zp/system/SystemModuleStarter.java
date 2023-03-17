package xyz.chener.zp.system;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.chener.zp.common.config.feign.loadbalance.NormalLoadBalanceAutoConfiguration;

import java.util.UUID;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/16:35
 * @Email: chen@chener.xyz
 */

@SpringBootApplication//(exclude = RedissonAutoConfiguration.class)
@EnableFeignClients
@EnableTransactionManagement
public class SystemModuleStarter {
    public static final String APP_UID = UUID.randomUUID().toString().replace("-", "");

    public static void main(String[] args) {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication app = new SpringApplication(SystemModuleStarter.class);
//        app.setApplicationStartup(new BufferingApplicationStartup(1024));
        app.run(args);
    }
}
