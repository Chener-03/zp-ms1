package xyz.chener.zp.zpusermodule.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/03/16:03
 * @Email: chen@chener.xyz
 */
@Configuration
public class Test2 implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new Test());
    }
}
