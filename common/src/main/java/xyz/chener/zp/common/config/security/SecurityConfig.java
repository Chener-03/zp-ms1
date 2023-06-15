package xyz.chener.zp.common.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xyz.chener.zp.common.autoconfig.CommonAutoConfig;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.writeList.WriteListAutoConfig;

import java.util.ArrayList;

@Configuration
@EnableMethodSecurity(prePostEnabled =true)
@EnableConfigurationProperties(CommonConfig.class)
@AutoConfigureAfter(CommonAutoConfig.class)
@AutoConfigureBefore(value = {SecurityFilterAutoConfiguration.class
        , SecurityAutoConfiguration.class},
name={"org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration"})
public class SecurityConfig {


    private final AuthFilter authFilter  ;
    private final CommonConfig commonConfig;
    private final AccessDeniedProcess accessDeniedProcess;
    private final EntryPointProcess entryPointProcess;

    public SecurityConfig(AuthFilter authFilter
            , CommonConfig commonConfig
            , AccessDeniedProcess accessDeniedProcess
            , EntryPointProcess entryPointProcess) {
        this.authFilter = authFilter;
        this.commonConfig = commonConfig;
        this.accessDeniedProcess = accessDeniedProcess;
        this.entryPointProcess = entryPointProcess;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }



    @Bean
    @RefreshScope
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ArrayList<String> urls = new ArrayList<>();
        urls.addAll(commonConfig.getSecurity().getWriteList());
        urls.addAll(WriteListAutoConfig.writeList);
        String[] writeList = urls.toArray(new String[0]);

        http.formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(cfg -> cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(cfg -> cfg.requestMatchers(writeList).permitAll().anyRequest().authenticated())
                .exceptionHandling(cfg -> cfg.authenticationEntryPoint(entryPointProcess)
                        .accessDeniedHandler(accessDeniedProcess)).addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }


}
