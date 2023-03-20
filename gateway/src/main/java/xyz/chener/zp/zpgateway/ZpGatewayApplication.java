package xyz.chener.zp.zpgateway;

import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = SentinelEndpointAutoConfiguration.class)
@EnableFeignClients
@RestController
public class ZpGatewayApplication {
    public static void main(String[] args) {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(ZpGatewayApplication.class, args);
    }
}
