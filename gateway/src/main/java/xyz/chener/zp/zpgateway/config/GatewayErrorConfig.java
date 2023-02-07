package xyz.chener.zp.zpgateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import xyz.chener.zp.zpgateway.common.entity.R;

import java.util.Map;

/**
 * @Author: chenzp
 * @Date: 2023/01/13/10:29
 * @Email: chen@chener.xyz
 */


@Configuration
@EnableConfigurationProperties({ServerProperties.class, WebProperties.class})
@Slf4j
public class GatewayErrorConfig {

    private final ServerProperties serverProperties;

    public GatewayErrorConfig(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Bean
    @Order(-1)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes,
                                                             WebProperties webProperties, ObjectProvider<ViewResolver> viewResolvers,
                                                             ServerCodecConfigurer serverCodecConfigurer, ApplicationContext applicationContext) {
        DefaultErrorWebExceptionHandler exceptionHandler = new GatewayExceptionHandle(errorAttributes,
                webProperties.getResources(), this.serverProperties.getError(), applicationContext);
        exceptionHandler.setViewResolvers(viewResolvers.orderedStream().toList());
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }


    public static class GatewayExceptionHandle extends DefaultErrorWebExceptionHandler{
        public GatewayExceptionHandle(ErrorAttributes errorAttributes, WebProperties.Resources resources, ErrorProperties errorProperties, ApplicationContext applicationContext) {
            super(errorAttributes, resources, errorProperties, applicationContext);
        }

        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
            return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
        }

        @Override
        protected int getHttpStatus(Map<String, Object> errorAttributes) {
            return R.HttpCode.HTTP_OK.get();
        }

        @Override
        protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
            Throwable error = super.getError(request);
            if (error instanceof NotFoundException exception)
            {
                return R.Builder.getInstance()
                        .setCode(R.HttpCode.HTTP_SERVER_MISS.get())
                        .setMessage(R.ErrorMessage.HTTP_SERVER_MISS.get())
                        .build().toMap();
            }

            if (error instanceof ResponseStatusException exception)
            {
                return R.Builder.getInstance()
                        .setCode(R.HttpCode.HTTP_PAGE_NOT_FOND.get())
                        .setMessage(R.ErrorMessage.HTTP_PAGE_NOT_FOND.get())
                        .build().toMap();
            }

            if (error instanceof Exception exception)
            {
                log.error(error.getMessage());
                return R.Builder.getInstance()
                        .setCode(R.HttpCode.HTTP_ERR.get())
                        .setMessage(R.ErrorMessage.HTTP_ERR.get())
                        .build().toMap();
            }

            return super.getErrorAttributes(request, options);
        }
    }


}
