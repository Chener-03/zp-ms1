package xyz.chener.zp.logger.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: chenzp
 * @Date: 2023/03/15/11:16
 * @Email: chen@chener.xyz
 */
public class ServerIdClassicConverter extends ClassicConverter {

    private static final AtomicReference<String> serverId = new AtomicReference<>(null);




    @Override
    public String convert(ILoggingEvent event) {
        getServerId();
        return serverId.get();
    }

    private void getServerId() {
        if (serverId.get() == null){
            synchronized (serverId) {
                ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
                if (applicationContext != null) {
                    serverId.set(applicationContext.getEnvironment().getProperty("spring.application.name"));
                }
            }
        }
    }

}
