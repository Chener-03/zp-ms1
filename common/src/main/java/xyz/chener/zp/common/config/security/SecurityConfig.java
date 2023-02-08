package xyz.chener.zp.common.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xyz.chener.zp.common.autoconfig.CommonAutoConfig;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.WriteListAutoConfig;

import java.util.ArrayList;

@Configuration
@EnableMethodSecurity(prePostEnabled =true)
@EnableConfigurationProperties(CommonConfig.class)
@AutoConfigureAfter(CommonAutoConfig.class)
@AutoConfigureBefore(value = {SecurityFilterAutoConfiguration.class, SecurityAutoConfiguration.class})
public class SecurityConfig {


    private final AuthFilter authFilter  ;
    private final CommonConfig commonConfig;
    private final AccessDeniedProcess accessDeniedProcess;
    private final EntryPointProcess entryPointProcess;

    public SecurityConfig(AuthFilter authFilter, CommonConfig commonConfig, AccessDeniedProcess accessDeniedProcess, EntryPointProcess entryPointProcess) {
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

        http.formLogin().disable()
                .logout().disable()
                .anonymous().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .requestMatchers(writeList)
                .permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling()
                .authenticationEntryPoint(entryPointProcess)
                .accessDeniedHandler(accessDeniedProcess)
                .and()
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .cors().and()
                .csrf().disable();
        return http.build();
    }


}
