package xyz.chener.zp.zpgateway.config;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpgateway.common.entity.CommonVar;
import xyz.chener.zp.zpgateway.utils.HeaderUtils;

import java.net.InetSocketAddress;
import java.util.Objects;


@Component
public class GatewayIpConfigForWebsocket extends AbstractGatewayFilterFactory<Object> {

    @Override
    public String name() {
        return "xyz.chener.zp.zpgateway.config.GatewayIpConfigForWebsocket.WebsocketIpGatewayFilter";
    }

    @Override
    public GatewayFilter apply(Object config) {
        return new WebsocketIpGatewayFilter();
    }

    public static class WebsocketIpGatewayFilter implements GatewayFilter{
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            if (ObjectUtils.nullSafeEquals(exchange.getRequest().getHeaders().getFirst("Upgrade"), "websocket")) {
                String ipString = getIpString(exchange.getRequest());
                ServerWebExchange newExchange = exchange;
                if (Objects.nonNull(ipString)) {
                    newExchange = HeaderUtils.addReactiveHeader(exchange, CommonVar.IP_HEAD,ipString);
                }
                return chain.filter(newExchange);
            }
            return chain.filter(exchange);
        }


        private String getIpString(ServerHttpRequest request)
        {
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            if (Objects.nonNull(remoteAddress))
                return remoteAddress.getHostString();
            return null;
        }


    }

}
