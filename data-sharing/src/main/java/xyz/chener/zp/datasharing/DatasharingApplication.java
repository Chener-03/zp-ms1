package xyz.chener.zp.datasharing;


import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.aviator.AviatorEvaluator;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import xyz.chener.zp.common.utils.MapBuilder;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.datasharing.config.DataSharingSourceConfig;
import xyz.chener.zp.datasharing.entity.thirdparty.UserBase;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.*;
import xyz.chener.zp.datasharing.requestProcess.exec.AuthExec;
import xyz.chener.zp.datasharing.requestProcess.exec.InJsExec;
import xyz.chener.zp.datasharing.requestProcess.exec.SqlExec;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication(exclude = SentinelEndpointAutoConfiguration.class)
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
