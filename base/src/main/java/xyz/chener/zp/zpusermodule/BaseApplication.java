package xyz.chener.zp.zpusermodule;


import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.chener.zp.common.config.feign.loadbalance.NormalLoadBalanceAutoConfiguration;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@LoadBalancerClients({
        @LoadBalancerClient(name = "zp-base-module",configuration = NormalLoadBalanceAutoConfiguration.class)
})
@Configuration
public class BaseApplication {


    @Bean
    TomcatProtocolHandlerCustomizer tomcatProtocolHandlerCustomizer(){
        return new TomcatProtocolHandlerCustomizer() {
            @Override
            public void customize(ProtocolHandler protocolHandler) {
                protocolHandler.setExecutor(new VirtualThreadExecutor(""));
            }
        };
    }


    public static void main(String[] args)  {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(BaseApplication.class, args);
    }

}
