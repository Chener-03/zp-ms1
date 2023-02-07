package xyz.chener.zp.zpstoragecalculation.context;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.chener.zp.zpstoragecalculation.config.StorageProperties;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class InitContext implements CommandLineRunner {

    private final StorageProperties storageProperties;


    public InitContext(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Bean
    @ConditionalOnProperty(value = "spring.cloud.nacos.discovery.watch.enabled",matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosDiscoveryProperties) {
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        if(metadata==null){
            metadata = new HashMap<>();
            nacosDiscoveryProperties.setMetadata(metadata);
        }
        metadata.put(StorageProperties.HARDWARE_UID,storageProperties.getHardwareUid());
        return new NacosWatch(nacosServiceManager,nacosDiscoveryProperties);
    }


    @Override
    public void run(String... args) throws Exception {
        File file = new File(storageProperties.getLocation());
        if (!file.isDirectory() || !file.exists()) {
            file.mkdirs();
        }
    }
}
