package xyz.chener.zp.zpgateway.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpgateway.common.entity.CommonVar;
import xyz.chener.zp.zpgateway.common.entity.R;
import xyz.chener.zp.zpgateway.config.nacoslistener.Auth2FaListener;
import xyz.chener.zp.zpgateway.entity.Auth2FaRegisterMetadata;
import xyz.chener.zp.zpgateway.service.UserModuleService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static xyz.chener.zp.zpgateway.common.entity.CommonVar.FA_HEADER_KEY;

@Slf4j
public class Auth2FaRepository implements WebFilter {


    private final UserModuleService userModuleService;

    public Auth2FaRepository(UserModuleService userModuleService) {
        this.userModuleService = userModuleService;
    }



    private Integer check2Fa(String faCode,String userBase64,boolean required,boolean hasHeader){
        String user = new String(Base64.getDecoder().decode(userBase64));
        return userModuleService.verify2Fa(faCode,user,required,hasHeader);
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


    private Mono<Void> getAuth2FaRequireResponse(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatusCode.valueOf(R.HttpCode.HTTP_OK.get()));
        ObjectMapper om = new ObjectMapper();
        byte[] bytes;
        try {
            bytes = om.writeValueAsString(R.Builder.getInstance().setCode(R.HttpCode.HTTP_2FA_REQUIRE_AUTH.get())
                            .setMessage(R.ErrorMessage.HTTP_2FA_REQUIRE_AUTH.get()).build().toMap())
                    .getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ignored) {
            bytes = new byte[0];
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        List<String> user = exchange.getRequest().getHeaders().get(CommonVar.REQUEST_USER);
        if (user == null || user.isEmpty()){
            return chain.filter(exchange);
        }

        ArrayList<Auth2FaRegisterMetadata> lsFaUrls = new ArrayList<>();
        Auth2FaListener.faUrls.values().forEach(lsFaUrls::addAll);

        Auth2FaRegisterMetadata auth2FaRegisterMetadata = lsFaUrls.stream()
                .filter(mtd -> exchange.getRequest().getURI().toString().contains(mtd.getUrl()))
                .findFirst().orElse(null);

        if (auth2FaRegisterMetadata == null){
            return chain.filter(exchange);
        }

        final boolean containsHeader = exchange.getRequest().getHeaders().containsKey(FA_HEADER_KEY);
        AtomicReference<String> faCode = new AtomicReference<>("");
        Optional.ofNullable(exchange.getRequest().getHeaders().get(FA_HEADER_KEY)).ifPresent(ls->{
            if (!ls.isEmpty()){
                faCode.set(ls.get(0));
            }
        });

        return Mono.fromFuture(CompletableFuture.supplyAsync(()-> check2Fa(faCode.get(), user.get(0),auth2FaRegisterMetadata.getRequire(),containsHeader)))
        .defaultIfEmpty(Auth2FaRegisterMetadata.AuthResultCode.FAIL).flatMap(faresult->{

            switch (faresult){
                case Auth2FaRegisterMetadata.AuthResultCode.SUCCESS ->{
                    return chain.filter(exchange);
                }
                case Auth2FaRegisterMetadata.AuthResultCode.NEED_AUTH ->{
                    return getNotAuth2FaResponse(exchange);
                }
                case Auth2FaRegisterMetadata.AuthResultCode.REQUIRE_AUTH ->{
                    return getAuth2FaRequireResponse(exchange);
                }
                default ->  {
                    return getAuth2FaFailResponse(exchange);
                }
            }

        }).onErrorResume(err->{
            throw new RuntimeException(err);
        });
    }
}
