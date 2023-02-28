package xyz.chener.zp.zpusermodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import xyz.chener.zp.zpusermodule.utils.Ip2RegUtils;

import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
public class UserApplication {

    public static final String APP_UID = UUID.randomUUID().toString().replace("-", "");

    public static void main(String[] args) throws Exception {
        new Ip2RegUtils();
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(UserApplication.class, args);
    }

}
