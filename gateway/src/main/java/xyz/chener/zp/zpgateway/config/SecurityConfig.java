package xyz.chener.zp.zpgateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpgateway.common.config.CommonConfig;
import xyz.chener.zp.zpgateway.common.utils.Jwt;
import xyz.chener.zp.zpgateway.service.UserModuleService;

import java.util.ArrayList;


@Configuration
@RefreshScope
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CommonConfig commonConfig;
    private final Jwt jwt;
    private final UserModuleService userModuleService;

    public SecurityConfig(AuthenticationEntryPoint authenticationEntryPoint
            , CommonConfig commonConfig
            , @Qualifier("xyz.chener.zp.zpgateway.service.UserModuleService") UserModuleService userModuleService
            , Jwt jwt) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.commonConfig = commonConfig;
        this.jwt = jwt;
        this.userModuleService = userModuleService;
    }


    @Bean
    @RefreshScope
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http)
    {
        http.authorizeExchange(exchangeSpec -> {
                    ServerHttpSecurity.AuthorizeExchangeSpec e = exchangeSpec;
                    ArrayList<String> list = new ArrayList<>();
                    list.addAll(commonConfig.getSecurity().getWriteList());
                    WriteListListener.writeListMap.values().forEach(list::addAll);
                    for (String s : list) {
                        e = e.pathMatchers(s).permitAll();
                    }
                    e.anyExchange().access(new AuthorizationManager());
                }).httpBasic(ServerHttpSecurity.HttpBasicSpec::disable
                )
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable
                )
                .logout(ServerHttpSecurity.LogoutSpec::disable
                )
                .anonymous(ServerHttpSecurity.AnonymousSpec::disable
                )
                .exceptionHandling(cfg -> {
                    cfg.authenticationEntryPoint(authenticationEntryPoint);
                })
                .addFilterBefore(new SecurityRepository(userModuleService, jwt, commonConfig), SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    public static class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
        @Override
        public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
            return authentication.map(auth -> new AuthorizationDecision(true))
                    .defaultIfEmpty(new AuthorizationDecision(false));
        }
    }

    private CorsConfiguration corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("*");
        corsConfiguration.addAllowedOriginPattern("*");
        return corsConfiguration;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig());
        return source;
    }

}
