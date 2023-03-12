package xyz.chener.zp.common.config.paramDecryption;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import xyz.chener.zp.common.config.paramDecryption.core.ModelAttributeDecryResolver;
import xyz.chener.zp.common.config.paramDecryption.core.RequestBodyDecryResolver;
import xyz.chener.zp.common.config.paramDecryption.core.RequestParamDecryResolver;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/11:34
 * @Email: chen@chener.xyz
 */
public class ParamDecryAutoConfig  implements ApplicationListener<ApplicationStartedEvent> {
    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    public ParamDecryAutoConfig(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (isInit.compareAndSet(false,true)) {
            ArrayList<HandlerMethodArgumentResolver> list = new ArrayList<>();
            list.add(new ModelAttributeDecryResolver());
            list.add(new RequestParamDecryResolver());
            list.add(new RequestBodyDecryResolver());
            list.addAll(requestMappingHandlerAdapter.getArgumentResolvers());
            requestMappingHandlerAdapter.setArgumentResolvers(list);
        }
    }

}
