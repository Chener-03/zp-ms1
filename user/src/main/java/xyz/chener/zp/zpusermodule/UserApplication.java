package xyz.chener.zp.zpusermodule;

import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.chener.zp.common.config.feign.loadbalance.NormalLoadBalanceAutoConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@SpringBootApplication(exclude = SentinelEndpointAutoConfiguration.class)
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@LoadBalancerClients({
        @LoadBalancerClient(name = "zp-user-module",configuration = NormalLoadBalanceAutoConfiguration.class)
})
public class UserApplication  {


    public static void main(String[] args)  {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(UserApplication.class, args);
    }

}
