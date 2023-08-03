package xyz.chener.zp.zpgateway.config.nacoslistener;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Configuration
public class WriteListListener implements InstanceChangeInterface {

    private final GatewayProperties gatewayProperties;


    private ApplicationContext applicationContext;

    private final String WRITE_LIST_KEY = "WRITE_LISTS";
    private final String WRITE_LIST_DIVISION = "####";

    public static ConcurrentHashMap<String, CopyOnWriteArrayList<String>> writeListMap = new ConcurrentHashMap<>();


    public WriteListListener(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public void onChange(List<Instance> instances,String instanceName, RouteDefinition route) {
        if (instances.isEmpty()){
            CopyOnWriteArrayList<String> m = writeListMap.get(instanceName);
            if (Objects.nonNull(m))
                m.clear();
            writeListMap.remove(instanceName);
        }else {

            int i = new Random().nextInt(instances.size());
            Instance instance = instances.get(i);
            String s = instance.getMetadata().get(WRITE_LIST_KEY);
            List<String> l = Arrays.stream(s.split(WRITE_LIST_DIVISION))
                    .filter(StringUtils::hasText).toList();
            CopyOnWriteArrayList<String> urls = new CopyOnWriteArrayList<>();

            route.getPredicates().forEach(pd->{
                Map<String, String> uarg = pd.getArgs();
                if (Objects.nonNull(uarg))
                {
                    uarg.values().forEach(eus->{
                        l.forEach(s1 -> {
                            urls.add(eus.replace("/**",s1));
                        });
                    });
                }
            });
            writeListMap.put(instanceName,urls);
        }
    }


}
