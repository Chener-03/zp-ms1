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
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Configuration
public class NacosPropertiesListener implements CommandLineRunner, ApplicationContextAware {


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


    public NacosPropertiesListener(GatewayProperties gatewayProperties) {
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
        AtomicBoolean isFirstRefresh = new AtomicBoolean(true);
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
                                applicationContext.getBean(WriteListListener.class).onChange(instances,modelName,route);
                                applicationContext.getBean(Auth2FaListener.class).onChange(instances,modelName,route);

                                applicationContext.publishEvent(new RefreshEvent(this, null, "Nacos Properties Refresh Event"));
                                if (isFirstRefresh.get()) {
                                    waitRefreshListenerRunning();
                                }
                            }
                        }

                        private void waitRefreshListenerRunning(){
                            // 暂时解决 refresh 事件在 RefreshEventListener 未初始化完成时就发布的问题
                            CompletableFuture.runAsync(()->{
                                try {
                                    RefreshEventListener rel = applicationContext.getBean(RefreshEventListener.class);
                                    if (rel != null)
                                    {
                                        Field ready = RefreshEventListener.class.getDeclaredField("ready");
                                        boolean ac = ready.canAccess(rel);
                                        ready.setAccessible(true);
                                        AtomicBoolean o = (AtomicBoolean) ready.get(rel);
                                        ready.setAccessible(ac);
                                        int i = 0;
                                        while (!o.get() && i++ < 100)
                                        {
                                            Thread.sleep(500);
                                        }
                                        if (isFirstRefresh.compareAndSet(true,false)) {
                                            applicationContext.publishEvent(new RefreshEvent(this, null, "Nacos Properties Refresh Event"));
                                        }
                                    }
                                }catch (Exception ignored) {
                                    isFirstRefresh.set(false);
                                    log.error("Nacos Properties Refresh Listener Running error:{}",ignored.getMessage());
                                }
                            });
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
