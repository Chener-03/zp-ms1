package xyz.chener.zp.zpusermodule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import xyz.chener.zp.common.config.feign.loadbalance.NormalLoadBalanceAutoConfiguration;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.utils.Ip2RegUtils;

import java.time.Duration;
import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableTransactionManagement
@LoadBalancerClients({
        @LoadBalancerClient(name = "zp-user-module",configuration = NormalLoadBalanceAutoConfiguration.class)
})
public class UserApplication {

    public static final String APP_UID = UUID.randomUUID().toString().replace("-", "");


    public static void main(String[] args) throws Exception {
        Cache<Object, Object> c = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(10000))
                .removalListener(n->{
                    System.out.println(n);
                })
                .build();

        c.put("a", "a");

//        c.invalidateAll();
        //c.cleanUp();
        Object a = c.get("a", () -> "b");
        Object b = c.get("a", () -> "b");


        String sFunctionName = ObjectUtils.getSFunctionName(UserBase::getUsername);

        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(UserApplication.class, args);
    }

}
