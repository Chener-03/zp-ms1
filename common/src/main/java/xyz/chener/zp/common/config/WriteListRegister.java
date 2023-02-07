package xyz.chener.zp.common.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WriteListRegister {

    private final String WRITE_LIST_KEY = "WRITE_LISTS";
    private final String WRITE_LIST_DIVISION = "####";

    @Bean(name = "writeListRegisterNacosWatch")
    @ConditionalOnProperty(value = "spring.cloud.nacos.discovery.watch.enabled",matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosDiscoveryProperties) {
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        if(metadata==null){
            metadata = new HashMap<>();
            nacosDiscoveryProperties.setMetadata(metadata);
        }
        StringBuilder sb = new StringBuilder();
        WriteListAutoConfig.writeList.forEach(e->{
            sb.append(e).append(WRITE_LIST_DIVISION);
        });
        metadata.put(WRITE_LIST_KEY,sb.toString());
        return new NacosWatch(nacosServiceManager,nacosDiscoveryProperties);
    }

}
