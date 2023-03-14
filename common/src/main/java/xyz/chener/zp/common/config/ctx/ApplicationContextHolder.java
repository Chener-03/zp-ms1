package xyz.chener.zp.common.config.ctx;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestContextHolder;

public class ApplicationContextHolder implements ApplicationContextAware {

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private static ApplicationContext applicationContext = null;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;

    }
}
