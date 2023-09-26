package xyz.chener.zp.task.config

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.annotation.Configuration


@Configuration
class Test : BeanDefinitionRegistryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {

    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        println()
    }
}