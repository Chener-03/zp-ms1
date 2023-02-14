package xyz.chener.zp.zpstoragecalculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import xyz.chener.zp.zpstoragecalculation.config.feign.loadbalance.LoadbalanceConfig;

@SpringBootApplication
@EnableFeignClients
@LoadBalancerClients({
        @LoadBalancerClient(name = "zp-storagecalculation-module",configuration = LoadbalanceConfig.class)
})
public class ZpStorageCalculationApplication {

    public static void main(String[] args) {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(ZpStorageCalculationApplication.class, args);
    }

}
