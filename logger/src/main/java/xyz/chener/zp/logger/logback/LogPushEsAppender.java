package xyz.chener.zp.logger.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.chener.zp.logger.config.elasticsearch.LoggerPush;
import xyz.chener.zp.logger.logback.entity.LogEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenzp
 * @Date: 2023/03/15/15:42
 * @Email: chen@chener.xyz
 */
public class LogPushEsAppender extends ConsoleAppender<ILoggingEvent> {

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (eventObject != null && isStarted()){
            String s = new String(getEncoder().encode(eventObject));
            if (queue != null){
                queue.offer(s);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
    }

    private static LinkedBlockingQueue<String> queue;

    private static LoggerPush loggerPush;

    public static void initQueue(LoggerPush loggerPush){
        LogPushEsAppender.loggerPush = loggerPush;
        queue = new LinkedBlockingQueue<>(10000);
        Thread t = new Thread(LogPushEsAppender::run);
        t.setName("LogPushEsAppender Deamon Thread");
        t.setDaemon(true);
        t.start();
    }

    private static void run(){
        ArrayList<LogEntity> logEntities = new ArrayList<>(1001);
        ObjectMapper om = new ObjectMapper();
        long lastTime = 0L;
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        while (!Thread.interrupted()){
            try {
                if(logEntities.size() >= 1000 || System.currentTimeMillis() - lastTime >= 60*1000) {
                    if (!logEntities.isEmpty()) {
                        loggerPush.add(logEntities, logEntities::clear);
                    }
                    lastTime = System.currentTimeMillis();
                }

                String s = queue.poll(1, TimeUnit.MINUTES);
                if (s != null){
                    logEntities.add(getLogEntity(s,om));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static LogEntity getLogEntity(String s,ObjectMapper om){
        try {
            return om.readValue(s, LogEntity.class);
        } catch (JsonProcessingException e) {
            int i1 = s.indexOf("\"message\":");
            int i2 = s.lastIndexOf("\"");
            s = s.substring(0,i1) + "\"message\":\"" + s.substring(i1+11,i2).replace("\"","'") + s.substring(i2);
            s = s.replace("\n","\\n");
            try {
                return om.readValue(s, LogEntity.class);
            } catch (JsonProcessingException ignored) { }
        }
        throw new RuntimeException("LogEntity parse error");
    }


}
