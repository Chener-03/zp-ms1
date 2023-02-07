package xyz.chener.zp.zpgateway.common.config;

import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: chenzp
 * @Date: 2023/01/12/13:50
 * @Email: chen@chener.xyz
 */

@Configuration
public class FeignClientConfig {

    @Bean
    public Decoder feignResultDecoder()
    {
        return new FeignResultDecoder();
    }

    @Bean
    public FeignRequestInterceptor feignRequestInterceptor(CommonConfig commonConfig)
    {
        return new FeignRequestInterceptor(commonConfig);
    }

}
