package xyz.chener.zp.zpgateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpgateway.common.entity.R;

/**
 * @Author: chenzp
 * @Date: 2023/01/12/16:10
 * @Email: chen@chener.xyz
 */

@Slf4j
@Component
public class AuthenticationEntryPoint extends HttpBasicServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        R<Object> r = R.Builder.getInstance().setCode(R.HttpCode.HTTP_NO_LOGIN.get())
                .setMessage(R.ErrorMessage.HTTP_NO_LOGIN.get()).build();
        ObjectMapper objectMapper = new ObjectMapper();
        DataBuffer bodyDataBuffer = null;
        try {
            bodyDataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(r));
        } catch (JsonProcessingException e) {
            log.error("401 write error  "+e.getMessage());
            byte[] b = {0};
            bodyDataBuffer = response.bufferFactory().wrap(b);
        }
        return response.writeWith(Mono.just(bodyDataBuffer));
    }
}
