package xyz.chener.zp.datasharing;


import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.datasharing.entity.thirdparty.UserBase;

@SpringBootApplication(exclude = SentinelEndpointAutoConfiguration.class)
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
public class DatasharingApplication {
    public static void main(String[] args) throws Exception {

        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(DatasharingApplication.class, args);
    }
}
