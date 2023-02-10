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
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import xyz.chener.zp.zpgateway.common.config.CommonConfig;
import xyz.chener.zp.zpgateway.common.entity.CommonVar;
import xyz.chener.zp.zpgateway.common.entity.LoginUserDetails;
import xyz.chener.zp.zpgateway.common.entity.R;
import xyz.chener.zp.zpgateway.common.entity.vo.PageInfo;
import xyz.chener.zp.zpgateway.common.error.HttpErrorException;
import xyz.chener.zp.zpgateway.common.utils.AssertUrils;
import xyz.chener.zp.zpgateway.common.utils.Jwt;
import xyz.chener.zp.zpgateway.entity.vo.Role;
import xyz.chener.zp.zpgateway.entity.vo.UserBase;
import xyz.chener.zp.zpgateway.error.TokenOverdueException;
import xyz.chener.zp.zpgateway.error.UserAuthNotFoundError;
import xyz.chener.zp.zpgateway.error.UserNotFoundError;
import xyz.chener.zp.zpgateway.service.UserModuleService;
import xyz.chener.zp.zpgateway.utils.HeaderUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

     public SecurityRepository(){
        userModuleService=null;
        jwt=null;
        commonConfig=null;
    }

    public SecurityRepository(@Qualifier("xyz.chener.zp.zpgateway.service.UserModuleService") UserModuleService userModuleService, Jwt jwt, @Qualifier("commonConfig") CommonConfig commonConfig)
    {
        this.userModuleService = userModuleService;
        this.jwt = jwt;
        this.commonConfig = commonConfig;
    }

    private boolean checkUser(UserBase userBase)
    {
        return !(userBase.getDisable().equals(1) || userBase.getExpireTime().getTime() <= new Date().getTime());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (processWriteList(exchange))
            return chain.filter(exchange);

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        LoginUserDetails userDetails = jwt.decode(token);

        if (Objects.nonNull(userDetails))
        {
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
                                            ServerWebExchange newExchange = HeaderUtils.addReactiveHeader(exchange
                                                    , CommonVar.REQUEST_USER, userBase.getUsername()
                                                    ,CommonVar.REQUEST_USER_AUTH, role.getPermissionEnNameList());
                                            Context ctx = ReactiveSecurityContextHolder.withAuthentication(
                                                    new UsernamePasswordAuthenticationToken(
                                                            userBase.getUsername(), null,
                                                            AuthorityUtils.commaSeparatedStringToAuthorityList(role.getPermissionEnNameList()))
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
            /*            try {
                CompletableFuture<PageInfo<UserBase>> result = CompletableFuture.supplyAsync(() -> userModuleService.getUserBaseInfoByName(userDetails.getUsername()));
                PageInfo<UserBase> userBaseInfo = result.get();
                if (userBaseInfo.getList().size() == 0)
                    throw new UserNotFoundError();
                UserBase userBase = userBaseInfo.getList().get(0);
                AssertUrils.state(Objects.equals(userDetails.getDs(),userBase.getDs()), TokenOverdueException.class);
                if (checkUser(userBase)) {
                    CompletableFuture<PageInfo<Role>> res = CompletableFuture.supplyAsync(() -> userModuleService.getUserRoleById(userBase.getRoleId()));
                    PageInfo<Role> rolePageInfo = res.get();
                    if (rolePageInfo.getList().size() == 0)
                        throw new UserAuthNotFoundError();
                    Role role = rolePageInfo.getList().get(0);
                    ServerWebExchange newExchange = HeaderUtils.addReactiveHeader(exchange
                            , CommonVar.REQUEST_USER, userBase.getUsername()
                            ,CommonVar.REQUEST_USER_AUTH, role.getPermissionEnNameList());
                    Context ctx = ReactiveSecurityContextHolder.withAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    userBase.getUsername(), null,
                                    AuthorityUtils.commaSeparatedStringToAuthorityList(role.getPermissionEnNameList()))
                    );
                    return chain.filter(newExchange).contextWrite(ctx);
                }
            }catch (Exception exception)
            {
                log.error(exception.getMessage());
                if (exception instanceof HttpErrorException ex)
                    return getResponseMono(exchange,ex);
            }*/
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

    private boolean matchUrl(String uri, String s) {
        if (s.contains("/**"))
        {
            int i = s.indexOf("/**");
            if (i == 0)
            {
                return true;
            } else
            {
                s = s.substring(0, i);
                if (uri.contains(s))
                {
                    return true;
                }
            }
        } else
        {
            if (uri.contains(s))
            {
                return true;
            }
        }
        return false;
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

