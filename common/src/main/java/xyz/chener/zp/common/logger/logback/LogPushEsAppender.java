package xyz.chener.zp.common.logger.logback;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/15/15:42
 * @Email: chen@chener.xyz
 */
public class LogPushEsAppender extends ConsoleAppender<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent eventObject) {
        if (eventObject != null && isStarted()){
            String s = new String(getEncoder().encode(eventObject));
            System.out.println();
        }
    }

}
