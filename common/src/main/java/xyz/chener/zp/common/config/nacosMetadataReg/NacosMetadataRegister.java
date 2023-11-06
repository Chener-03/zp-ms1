package xyz.chener.zp.common.config.nacosMetadataReg;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import xyz.chener.zp.common.config.auth2fa.Auth2FaRegister;
import xyz.chener.zp.common.config.grayscalePublishing.GrayscalePublishTagRegister;
import xyz.chener.zp.common.config.writeList.WriteListRegister;

import java.util.*;


public class NacosMetadataRegister {

    public static final List<MetatadaRegInterface> metatadaRegInterfaceList = new ArrayList<>();

    public NacosMetadataRegister(GrayscalePublishTagRegister grayscalePublishTagRegister) {
        metatadaRegInterfaceList.add(WriteListRegister.getInstance());
        metatadaRegInterfaceList.add(Auth2FaRegister.getInstance());
        metatadaRegInterfaceList.add(grayscalePublishTagRegister);
    }

    @Bean(name = "xyz.chener.zp.common.config.nacosMetadataReg.NacosMetadataRegister.nacosWatch")
    @ConditionalOnProperty(value = "spring.cloud.nacos.discovery.watch.enabled",matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosDiscoveryProperties) {
        Map<String, String> metadata = Optional.ofNullable(nacosDiscoveryProperties.getMetadata()).orElse(new LinkedHashMap<>());
        metatadaRegInterfaceList.forEach(e->e.registerMetadata(metadata));
        nacosDiscoveryProperties.setMetadata(metadata);
        return new NacosWatch(nacosServiceManager,nacosDiscoveryProperties);
    }

}
