package xyz.chener.zp.zpgateway.config.nacoslistener;


import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpgateway.common.entity.R;
import xyz.chener.zp.zpgateway.service.UserModuleService;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.chener.zp.zpgateway.common.entity.CommonVar.FA_HEADER_KEY;


@Slf4j
@Configuration
public class Auth2FaConfig implements InstanceChangeInterface  {

    public static final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> faUrls = new ConcurrentHashMap<>();

    private UserModuleService userModuleService;

    private final String KEY = "2FA_URL_LIST";
    private final String DIVISION = "####";

    @Autowired
    @Qualifier("xyz.chener.zp.zpgateway.service.UserModuleService")
    @Lazy
    public void setUserModuleService(UserModuleService userModuleService) {
        this.userModuleService = userModuleService;
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE-98)
    public GlobalFilter auth2FaFilter()
    {
        return (exchange, chain) -> {
            ArrayList<String> lsFaUrls = new ArrayList<>();
            faUrls.values().forEach(lsFaUrls::addAll);

            if (lsFaUrls.stream().noneMatch(url -> exchange.getRequest().getURI().toString().equals(url))) {
                return chain.filter(exchange);
            }

            List<String> fas = exchange.getRequest().getHeaders().get(FA_HEADER_KEY);
            if (fas == null || fas.isEmpty()){
                return getNotAuth2FaResponse(exchange);
            }

            return check2Fa(fas.get(0)) ? chain.filter(exchange) : getAuth2FaFailResponse(exchange);
        };
    }

    private Boolean check2Fa(String fa){
        return false;
    }


    private Mono<Void> getNotAuth2FaResponse(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatusCode.valueOf(R.HttpCode.HTTP_OK.get()));
        ObjectMapper om = new ObjectMapper();
        byte[] bytes;
        try {
            bytes = om.writeValueAsString(R.Builder.getInstance().setCode(R.HttpCode.HTTP_2FA_NOT_AUTH.get())
                            .setMessage(R.ErrorMessage.HTTP_2FA_NOT_AUTH.get()).build().toMap())
                    .getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ignored) {
            bytes = new byte[0];
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> getAuth2FaFailResponse(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatusCode.valueOf(R.HttpCode.HTTP_OK.get()));
        ObjectMapper om = new ObjectMapper();
        byte[] bytes;
        try {
            bytes = om.writeValueAsString(R.Builder.getInstance().setCode(R.HttpCode.HTTP_2FA__AUTH_FAIL.get())
                            .setMessage(R.ErrorMessage.HTTP_2FA__AUTH_FAIL.get()).build().toMap())
                    .getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ignored) {
            bytes = new byte[0];
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public void onChange(List<Instance> instances, String instanceName, RouteDefinition route) {
        if (instances == null || instances.isEmpty()){
            CopyOnWriteArrayList<String> m = faUrls.get(instanceName);
            if (Objects.nonNull(m))
                m.clear();
            faUrls.remove(instanceName);
        }else {
            int i = new Random().nextInt(instances.size());
            Instance instance = instances.get(i);
            String s = instance.getMetadata().get(KEY);
            List<String> l = Arrays.stream(s.split(DIVISION))
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
            faUrls.put(instanceName,urls);
        }
    }
}
