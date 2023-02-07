package xyz.chener.zp.zpusermodule;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.utils.Jwt;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/07/14:48
 * @Email: chen@chener.xyz
 */

@Configuration
public class Test implements CommandLineRunner {

    @Autowired
    Jwt jwt;

    @Override
    public void run(String... args) throws Exception {

    }
}
