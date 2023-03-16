package xyz.chener.zp.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.UUID;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/16:35
 * @Email: chen@chener.xyz
 */

@SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement
public class SystemModuleStarter {
    public static final String APP_UID = UUID.randomUUID().toString().replace("-", "");

    public static void main(String[] args) {
        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(SystemModuleStarter.class, args);
    }
}
