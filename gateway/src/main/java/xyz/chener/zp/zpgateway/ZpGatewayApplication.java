package xyz.chener.zp.zpgateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import xyz.chener.zp.zpgateway.config.loadbalance.TagLoadBalanceRegister;


@SpringBootApplication
@EnableFeignClients
//@LoadBalancerClient(name = "zp-base-module",configuration = TagLoadBalanceRegister.TagLoadBalanceAutoConfig1.class)
public class ZpGatewayApplication {
    public static void main(String[] args) {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(ZpGatewayApplication.class, args);
    }
}
