package xyz.chener.zp.zpgateway.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
public class WriteListListener implements CommandLineRunner, ApplicationContextAware {

    private final GatewayProperties gatewayProperties;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    public String serverAddr;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    public String namespace;

    @Value("${spring.cloud.nacos.discovery.group}")
    public String group;

    @Value("${spring.cloud.nacos.discovery.username}")
    public String username;

    @Value("${spring.cloud.nacos.discovery.password}")
    public String password;

    private ApplicationContext applicationContext;

    private final String WRITE_LIST_KEY = "WRITE_LISTS";
    private final String WRITE_LIST_DIVISION = "####";

    public static ConcurrentHashMap<String, CopyOnWriteArrayList<String>> writeListMap = new ConcurrentHashMap<>();


    public WriteListListener(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", serverAddr);
        properties.setProperty("groupName", group);
        properties.setProperty("namespace", namespace);
        properties.setProperty("username", username);
        properties.setProperty("password", password);
        NamingService namingService = NacosFactory.createNamingService(properties);
        gatewayProperties.getRoutes().forEach(route ->{
            String uri =route.getUri().toString();
            if (uri.indexOf("lb://")==0) {
                String modelName = uri.substring(5);
                try {
                    namingService.subscribe(modelName,group, new EventListener() {
                        @Override
                        public void onEvent(Event event) {
                            if (event instanceof NamingEvent ev)
                            {
                                List<Instance> instances = ev.getInstances();
                                if (instances.isEmpty()) {
                                    writeListMap.get(modelName).clear();
                                    writeListMap.remove(modelName);
                                }else
                                {
                                    int i = new Random().nextInt(instances.size());
                                    Instance instance = instances.get(i);
                                    String s = instance.getMetadata().get(WRITE_LIST_KEY);
                                    List<String> l = Arrays.stream(s.split(WRITE_LIST_DIVISION))
                                            .filter(Objects::nonNull).toList();
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
                                    writeListMap.put(modelName,urls);
                                    applicationContext.publishEvent(new RefreshEvent(this, null, "writeList Refresh Event"));
                                }
                            }
                        }
                    });
                } catch (NacosException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
