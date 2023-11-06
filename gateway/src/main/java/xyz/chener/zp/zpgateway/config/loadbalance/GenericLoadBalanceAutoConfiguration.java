package xyz.chener.zp.zpgateway.config.loadbalance;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerEagerLoadProperties;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({ ReactorLoadBalancerClientAutoConfiguration.class,
        LoadBalancerBeanPostProcessorAutoConfiguration.class })
@ConditionalOnProperty(value = "spring.cloud.loadbalancer.enabled", havingValue = "true", matchIfMissing = true)
public class GenericLoadBalanceAutoConfiguration {

    @Bean
    public LoadBalancerClientFactory loadBalancerClientFactory(LoadBalancerClientsProperties properties,
                                                               ObjectProvider<List<LoadBalancerClientSpecification>> configurations) {
        var clientFactory = new LoadBalancerClientFactoryProxy(properties);
        clientFactory.setConfigurations(configurations.getIfAvailable(Collections::emptyList));
        return clientFactory;
    }

    public static class LoadBalancerClientFactoryProxy  extends LoadBalancerClientFactory{

        public LoadBalancerClientFactoryProxy(LoadBalancerClientsProperties properties) {
            super(properties);
        }

        @Override
        public GenericApplicationContext buildContext(String name) {
            if (StringUtils.hasText(name) && name.startsWith(GenericReactiveLoadBalancerClientFilter.LB_INSTANCE_PREFIX))
                name = name.substring(GenericReactiveLoadBalancerClientFilter.LB_INSTANCE_PREFIX.length());
            return super.buildContext(name);
        }

    }

}
