package xyz.chener.zp.common.config.requesturliterator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.auth2fa.Auth2FaRegister;
import xyz.chener.zp.common.config.writeList.WriteListRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RequestUrlBeanDefinitionIterator implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

    public static final List<UrlClassNotice> needToNotice = new ArrayList<>();

    public RequestUrlBeanDefinitionIterator(){
        needToNotice.add(Auth2FaRegister.getInstance());
        needToNotice.add(WriteListRegister.getInstance());
    }

    private String contextPath = null;

    @Override
    public void setEnvironment(Environment environment) {
        contextPath = environment.getProperty("server.context-path");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<? extends Class<?>> list = Arrays.stream(registry.getBeanDefinitionNames()).map(e -> {
            Class<?> clz = null;
            try {
                if (registry.getBeanDefinition(e).getBeanClassName() != null)
                    clz = Class.forName(registry.getBeanDefinition(e).getBeanClassName());
            } catch (Exception ignored) { }
            return clz;
        }).filter(e -> e != null && (e.getAnnotation(RestController.class) != null || e.getAnnotation(Controller.class) != null)).toList();
        needToNotice.forEach(urlClassNotice -> urlClassNotice.notice(list,contextPath));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }


}
