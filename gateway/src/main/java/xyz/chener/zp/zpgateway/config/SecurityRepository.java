package xyz.chener.zp.zpgateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import xyz.chener.zp.zpgateway.common.config.CommonConfig;
import xyz.chener.zp.zpgateway.common.entity.CommonVar;
import xyz.chener.zp.zpgateway.common.entity.LoginUserDetails;
import xyz.chener.zp.zpgateway.common.entity.R;
import xyz.chener.zp.zpgateway.common.error.HttpErrorException;
import xyz.chener.zp.zpgateway.common.utils.AssertUrils;
import xyz.chener.zp.zpgateway.common.utils.Jwt;
import xyz.chener.zp.zpgateway.config.nacoslistener.WriteListListener;
import xyz.chener.zp.zpgateway.entity.SecurityVar;
import xyz.chener.zp.zpgateway.entity.vo.Role;
import xyz.chener.zp.zpgateway.entity.vo.UserBase;
import xyz.chener.zp.zpgateway.error.SystemCheckError;
import xyz.chener.zp.zpgateway.error.TokenOverdueException;
import xyz.chener.zp.zpgateway.error.UserAuthNotFoundError;
import xyz.chener.zp.zpgateway.error.UserNotFoundError;
import xyz.chener.zp.zpgateway.service.UserModuleService;
import xyz.chener.zp.zpgateway.utils.HeaderUtils;
import xyz.chener.zp.zpgateway.utils.UriMatcherUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Author: chenzp
 * @Date: 2023/01/12/16:02
 * @Email: chen@chener.xyz
 */

//@Component
@Slf4j
public class SecurityRepository implements WebFilter {

    private final UserModuleService userModuleService;
    private final Jwt jwt;
    private final CommonConfig commonConfig;
    private String actuatorPath = "/actuator";


    public SecurityRepository(@Qualifier("xyz.chener.zp.zpgateway.service.UserModuleService") UserModuleService userModuleService, Jwt jwt
            , @Qualifier("commonConfig") CommonConfig commonConfig)
    {
        this.userModuleService = userModuleService;
        this.jwt = jwt;
        this.commonConfig = commonConfig;
    }

    private boolean checkUser(UserBase userBase)
    {
        return !(userBase.getDisable().equals(1) || userBase.getExpireTime().getTime() <= new Date().getTime());
    }

    private boolean processActuator(ServerWebExchange webExchange)
    {
        try {
            boolean b = webExchange.getRequest().getHeaders()
                    .get(CommonVar.OPEN_FEIGN_HEADER).stream()
                    .anyMatch(s -> s.equals(commonConfig.getSecurity().getFeignCallSlat()));
            if (!b)
                return false;
        }catch (Exception e){
            return false;
        }
        return webExchange.getRequest().getPath().toString().startsWith(actuatorPath);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (processWriteList(exchange))
            return chain.filter(exchange);
        if (processActuator(exchange)){
            Context ctx = ReactiveSecurityContextHolder.withAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            "Actuator", null,
                            AuthorityUtils.commaSeparatedStringToAuthorityList("Actuator"))
            );
            return chain.filter(exchange).contextWrite(ctx);
        }

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        final LoginUserDetails userDetails = jwt.decode(token);

        if (Objects.nonNull(userDetails))
        {
            if (!checkJwtBindSystem(userDetails,exchange.getRequest().getPath().toString())) {
                return getResponseMono(exchange,new SystemCheckError());
            }

            return Mono.fromFuture(CompletableFuture.supplyAsync(() -> userModuleService.getUserBaseInfoByName(userDetails.getUsername())))
                    .flatMap(userBaseInfo -> {
                        if (userBaseInfo.getList().size() == 0)
                            return getResponseMono(exchange,new UserNotFoundError());
                        UserBase userBase = userBaseInfo.getList().get(0);
                        try {
                            AssertUrils.state(Objects.equals(userDetails.getDs(),userBase.getDs()), TokenOverdueException.class);
                            if (checkUser(userBase)) {
                                return Mono.fromFuture(CompletableFuture.supplyAsync(() -> userModuleService.getUserRoleById(userBase.getRoleId())))
                                        .flatMap(rolePageInfo -> {
                                            if (rolePageInfo.getList().size() == 0)
                                                return getResponseMono(exchange,new UserAuthNotFoundError());
                                            Role role = rolePageInfo.getList().get(0);

                                            List<String> roleAuthList = Arrays.stream(role.getPermissionEnNameList().split(",")).filter(s -> StringUtils.hasText(s) && s.startsWith(SecurityVar.ROLE_PREFIX)).toList();
                                            try {
                                                String s = new ObjectMapper().writeValueAsString(userDetails);
                                            } catch (Exception e) { }
                                            String base64Username = Base64.getEncoder().encodeToString(userBase.getUsername().getBytes(StandardCharsets.UTF_8));

                                            ServerWebExchange newExchange = HeaderUtils.addReactiveHeader(exchange
                                                    , CommonVar.REQUEST_USER, base64Username
                                                    ,CommonVar.REQUEST_USER_AUTH, String.join(",",roleAuthList));
                                            Context ctx = ReactiveSecurityContextHolder.withAuthentication(
                                                    new UsernamePasswordAuthenticationToken(
                                                            userBase.getUsername(), null,
                                                            AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",",roleAuthList)))
                                            );
                                            return chain.filter(newExchange).contextWrite(ctx);
                                        });
                            }
                        }catch (Exception ex){
                            log.error(ex.getMessage());
                            if (ex instanceof HttpErrorException e)
                                return getResponseMono(exchange,e);
                        }
                        return chain.filter(exchange);
                    });
        }

        return chain.filter(exchange);
    }

    private boolean processWriteList(ServerWebExchange exchange)
    {
        String uri = exchange.getRequest().getPath().toString();
        List<String> list = commonConfig.getSecurity().getWriteList();

        for (CopyOnWriteArrayList<String> value : WriteListListener.writeListMap.values()) {
            for (String s : value)
            {
                if (matchUrl(uri, s)) return true;
            }
        }
        for (String s : list)
        {
            if (matchUrl(uri, s)) return true;
        }
        return false;
    }

    private boolean checkJwtBindSystem(LoginUserDetails details,String urlPath){
        switch (details.getSystem()) {
            case LoginUserDetails.SystemEnum.WEB -> {
                return urlPath.contains(CommonVar.WEB_URL_PREFIX);
            }
            case LoginUserDetails.SystemEnum.MOBILE -> {
                return urlPath.contains(CommonVar.CLIENT_URL_PREFIX);
            }
        }
        return true;
    }

    private boolean matchUrl(String uri, String s) {
        if (s.contains("{") && s.contains("}"))
        {
            s = s.substring(0, s.indexOf("{")) + "*" + s.substring(s.indexOf("}") + 1);
        }
        return UriMatcherUtils.match(s,uri);
    }

    private Mono<Void> getResponseMono(ServerWebExchange exchange, HttpErrorException exception) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatusCode.valueOf(R.HttpCode.HTTP_OK.get()));
        ObjectMapper om = new ObjectMapper();
        byte[] bytes;
        try {
            bytes = om.writeValueAsString(R.Builder.getInstance().setCode(exception.getHttpCode())
                            .setMessage(exception.getHttpErrorMessage()).build().toMap())
                    .getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException ignored) {
            bytes = new byte[0];
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }


}

