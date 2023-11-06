package xyz.chener.zp.zpgateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import xyz.chener.zp.zpgateway.config.loadbalance.GenericLoadBalanceConfiguration;

import static xyz.chener.zp.zpgateway.config.loadbalance.GenericReactiveLoadBalancerClientFilter.LB_INSTANCE_PREFIX;


@SpringBootApplication
@EnableFeignClients
@LoadBalancerClients({
        @LoadBalancerClient(name = LB_INSTANCE_PREFIX + "zp-base-module", configuration = GenericLoadBalanceConfiguration.class),
        @LoadBalancerClient(name = LB_INSTANCE_PREFIX + "zp-storage-v2-module", configuration = GenericLoadBalanceConfiguration.class),
        @LoadBalancerClient(name = LB_INSTANCE_PREFIX + "zp-system-module", configuration = GenericLoadBalanceConfiguration.class),
        @LoadBalancerClient(name = LB_INSTANCE_PREFIX + "zp-datasharing-module", configuration = GenericLoadBalanceConfiguration.class),
        @LoadBalancerClient(name = LB_INSTANCE_PREFIX + "zp-task-module", configuration = GenericLoadBalanceConfiguration.class),
})
public class ZpGatewayApplication {
    public static void main(String[] args) {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(ZpGatewayApplication.class, args);
    }
}
