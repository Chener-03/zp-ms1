package xyz.chener.zp.common.config.okhttpclient;

import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.web.service.annotation.HttpExchange;
import xyz.chener.zp.common.config.okhttpclient.error.OkHttpInterfaceProxyInitError;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author: chenzp
 * @Date: 2023/02/17/08:53
 * @Email: chen@chener.xyz
 */
public class HttpRequestInterfaceInject implements BeanDefinitionRegistryPostProcessor {


    private AbstractBeanDefinition processBeanDefinition(Class clazz)
    {
        AbstractBeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(clazz).getBeanDefinition();
        bd.getConstructorArgumentValues().addGenericArgumentValue(clazz);
        bd.setBeanClass(OkHttpInterfaceBeanFactory.class);
        bd.getPropertyValues().add("mapperInterface", clazz);
        bd.getPropertyValues().add("http", new RuntimeBeanReference(OkHttpClient.class));
        return bd;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] res = resolver.getResources("**/*.class");
            SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(resolver);
            Arrays.stream(res).filter(e -> {
                try {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(e);
                    HttpExchange ann = Class.forName(metadataReader.getClassMetadata().getClassName()).getAnnotation(HttpExchange.class);
                    return Objects.nonNull(ann);
                } catch (Throwable ignored) {}
                return false;
            }).map(e -> {
                try {
                    return metadataReaderFactory.getMetadataReader(e).getClassMetadata().getClassName();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).forEach(e->{
                try {
                    Class<?> clazz = Class.forName(e);
                    registry.registerBeanDefinition(clazz.getName(),processBeanDefinition(clazz));
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }catch (Exception exception){
            throw new OkHttpInterfaceProxyInitError(exception.getMessage());
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
