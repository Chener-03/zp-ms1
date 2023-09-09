package xyz.chener.zp.zpgateway.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpgateway.common.entity.CommonVar;
import xyz.chener.zp.zpgateway.common.entity.R;
import xyz.chener.zp.zpgateway.utils.HeaderUtils;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties(GatewayCostomProperties.class)
public class GatewayIpConfig {

    private final GatewayCostomProperties gatewayCostomProperties;

    public GatewayIpConfig(@Qualifier("gatewayCostomProperties") GatewayCostomProperties gatewayCostomProperties) {
        this.gatewayCostomProperties = gatewayCostomProperties;
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE-100)
    public GlobalFilter ipRecordFilter()
    {
        return (exchange, chain) -> {
            String ipString = getIpString(exchange.getRequest());
            ServerWebExchange newExchange = exchange;
            if (Objects.nonNull(ipString)) {
                newExchange = HeaderUtils.addReactiveHeader(exchange,CommonVar.IP_HEAD,ipString);
            }
            return chain.filter(newExchange);
        };
    }


    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE-99)
    public GlobalFilter ipFilter()
    {
        return (exchange, chain) -> {
            if (gatewayCostomProperties.getIp().getEnable() ) {
                String ipString = getIpString(exchange.getRequest());
                if (gatewayCostomProperties.getIp().getIsWhite())
                {
                    if (!getIpList().contains(ipString)) {
                        return getIpBanResponseMono(exchange);
                    }
                }else
                {
                    if (getIpList().contains(ipString)) {
                        return getIpBanResponseMono(exchange);
                    }
                }
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> getIpBanResponseMono(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatusCode.valueOf(R.HttpCode.HTTP_OK.get()));
        ObjectMapper om = new ObjectMapper();
        byte[] bytes;
        try {
            bytes = om.writeValueAsString(R.Builder.getInstance().setCode(R.HttpCode.HTTP_NO_ACCESS.get())
                            .setMessage(R.ErrorMessage.HTTP_IP_BAN.get()).build().toMap())
                    .getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ignored) {
            bytes = new byte[0];
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }


    private String getIpString(ServerHttpRequest request)
    {
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (Objects.nonNull(remoteAddress))
            return remoteAddress.getHostString();
        return null;
    }

    private List<String> getIpList()
    {
        return Collections.unmodifiableList(gatewayCostomProperties.getIp().getIpList());
    }

}
