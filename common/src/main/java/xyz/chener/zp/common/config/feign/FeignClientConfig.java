package xyz.chener.zp.common.config.feign;

import feign.Request;
import feign.Response;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.clientconfig.FeignClientConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.chener.zp.common.config.CommonConfig;

import java.util.concurrent.TimeUnit;

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

    @Bean
    public Request.Options options()
    {
        return new Request.Options(2, TimeUnit.SECONDS, 5, TimeUnit.SECONDS, true);
    }

    @Bean
    public Retryer feignRetryer()
    {
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 2);
    }

    @Bean
    public ErrorDecoder errorDecoder()
    {
        return new FeignRequestErrorDecoder();
    }

}
