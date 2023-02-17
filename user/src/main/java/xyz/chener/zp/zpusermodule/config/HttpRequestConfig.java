package xyz.chener.zp.zpusermodule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;
import xyz.chener.zp.zpusermodule.service.GoogleRequest;

/**
 * @Author: chenzp
 * @Date: 2023/02/06/14:40
 * @Email: chen@chener.xyz
 */

@Configuration
public class HttpRequestConfig {

/*    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultStatusHandler(HttpStatusCode::isError
                        , clientResponse -> Mono.error(new HttpErrorException(R.HttpCode.HTTP_ERR.get(),"http client error")))
                .build();
    }

    @Bean
    public GoogleRequest googleRequest() {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient())).build();
        return factory.createClient(GoogleRequest.class);
    }*/
}
