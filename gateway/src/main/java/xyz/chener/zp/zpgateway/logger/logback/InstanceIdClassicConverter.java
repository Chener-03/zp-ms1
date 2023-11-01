package xyz.chener.zp.zpgateway.logger.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/11:11
 * @Email: chen@chener.xyz
 */
public class InstanceIdClassicConverter extends ClassicConverter {

    private static final AtomicReference<String> instanceUid = new AtomicReference<>(null);

    @Override
    public String convert(ILoggingEvent event) {
        getServerId();
        return instanceUid.get();
    }

    private void getServerId() {
        if (instanceUid.get() == null) {
            synchronized (instanceUid) {
                instanceUid.set(System.getProperty("application.uid"));
            }
        }
    }

}
