package xyz.chener.zp.common.config.unifiedReturn;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.Optional;
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
            ArrayList<HandlerMethodReturnValueHandler> list = new ArrayList<>(Optional.ofNullable(requestMappingHandlerAdapter.getReturnValueHandlers()).orElse(new ArrayList<>()));
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getClass().equals(RequestResponseBodyMethodProcessor.class)){
                    list.add(i,new UnifiedReturnHandle());
                    break;
                }
            }
            requestMappingHandlerAdapter.setReturnValueHandlers(list);
        }
    }

}
