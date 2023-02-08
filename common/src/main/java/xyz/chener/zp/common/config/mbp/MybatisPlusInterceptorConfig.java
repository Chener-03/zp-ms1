package xyz.chener.zp.common.config.mbp;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @Author: chenzp
 * @Date: 2023/02/08/16:58
 * @Email: chen@chener.xyz
 */

@Configuration
@AutoConfigureAfter(MybatisPlusAutoConfiguration.class)
public class MybatisPlusInterceptorConfig {

    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }

    @Bean
    public CustomFieldsQueryInterceptor customFieldsQueryInterceptor(MybatisPlusInterceptor mybatisPlusInterceptor) {
        CustomFieldsQueryInterceptor interceptor = new CustomFieldsQueryInterceptor();
        ArrayList<InnerInterceptor> list = new ArrayList<>(mybatisPlusInterceptor.getInterceptors());
        list.add(interceptor);
        mybatisPlusInterceptor.setInterceptors(Collections.unmodifiableList(list));
        return interceptor;
    }

}
