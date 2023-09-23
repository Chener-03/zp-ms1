package xyz.chener.zp.datasharing;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.chener.zp.datasharing.config.DataSharingSourceConfig;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableConfigurationProperties({DataSharingSourceConfig.class})
public class DatasharingApplication {
    public static void main(String[] args) throws Exception {
        System.setProperty("csp.sentinel.log.output.type","console");
        System.setProperty("polyglot.engine.WarnInterpreterOnly","false");
        SpringApplication.run(DatasharingApplication.class, args);
    }
}
