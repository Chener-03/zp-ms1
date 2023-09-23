package xyz.chener.zp.common.config.vitureThread;


import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfigureBefore(name = "org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration")
@Configuration(proxyBeanMethods = false)
public class ServletVitureThreadPoolAutoConfiguration {

    public static final String VITURE_THREAD_POOL_NAME = "ServletVitureThreadPool-";

    @ConditionalOnProperty(name = "zp.enable-servlet-viture-thread",matchIfMissing = true)
    @Bean("xyz.chener.zp.common.config.vitureThread.ServletVitureThreadPoolAutoConfiguration.tomcatProtocolHandlerCustomizer")
    @ConditionalOnClass(value = {VirtualThreadExecutor.class})
    public TomcatProtocolHandlerCustomizer<ProtocolHandler> tomcatProtocolHandlerCustomizer(){
        return protocolHandler -> protocolHandler.setExecutor(new VirtualThreadExecutor(VITURE_THREAD_POOL_NAME));
    }

}
