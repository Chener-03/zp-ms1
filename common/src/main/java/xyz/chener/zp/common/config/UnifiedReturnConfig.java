package xyz.chener.zp.common.config;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: chenzp
 * @Date: 2023/01/11/15:28
 * @Email: chen@chener.xyz
 */

@Configuration
public class UnifiedReturnConfig implements ApplicationListener<ApplicationStartedEvent> {
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    public UnifiedReturnConfig(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (isInit.compareAndSet(false,true)) {
            ArrayList<HandlerMethodReturnValueHandler> list = new ArrayList<>();
            list.add(new UnifiedReturnHandle());
            list.addAll(requestMappingHandlerAdapter.getReturnValueHandlers());
            requestMappingHandlerAdapter.setReturnValueHandlers(list);
        }
    }

}
