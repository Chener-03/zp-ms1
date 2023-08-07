package xyz.chener.zp.zpgateway.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpgateway.common.entity.CommonVar;
import xyz.chener.zp.zpgateway.common.entity.R;
import xyz.chener.zp.zpgateway.config.nacoslistener.Auth2FaListener;
import xyz.chener.zp.zpgateway.service.UserModuleService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static xyz.chener.zp.zpgateway.common.entity.CommonVar.FA_HEADER_KEY;

@Slf4j
@Configuration
public class Auth2FaConfig {


    private UserModuleService userModuleService;

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
            List<String> user = exchange.getRequest().getHeaders().get(CommonVar.REQUEST_USER);
            if (user == null || user.isEmpty()){
                return chain.filter(exchange);
            }

            ArrayList<String> lsFaUrls = new ArrayList<>();
            Auth2FaListener.faUrls.values().forEach(lsFaUrls::addAll);

            if (lsFaUrls.stream().noneMatch(url -> exchange.getRequest().getURI().toString().equals(url))) {
                return chain.filter(exchange);
            }

            List<String> fas = exchange.getRequest().getHeaders().get(FA_HEADER_KEY);
            String faCode;
            if (fas == null || fas.isEmpty()){
                faCode = "";
            }else {
                faCode = fas.get(0);
            }

            Boolean fasuccess = check2Fa(faCode, user.get(0));
            if (fasuccess) {
                return chain.filter(exchange);
            }

            if (fas == null || fas.isEmpty()){
                return getNotAuth2FaResponse(exchange);
            }
            return getAuth2FaFailResponse(exchange);
        };
    }



    private Boolean check2Fa(String faCode,String userBase64){
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

}
