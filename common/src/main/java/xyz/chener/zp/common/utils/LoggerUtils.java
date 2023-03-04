package xyz.chener.zp.common.utils;

import org.slf4j.Logger;

public class LoggerUtils {
    public static void logErrorStackTrace(Throwable e, Logger logger) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder sb = new StringBuilder();
        if (stackTrace.length > 0) {
            sb.append("StackTrace:\n");
            for (int i = 0; i < e.getStackTrace().length; i++) {
                sb.append(stackTrace[i].toString()).append("\n");
                if (i >20){
                    sb.append("......\n");
                    break;
                }
            }
        }
        logger.error(String.format("Message:%s\n\nCause:%s\n\n%s", e.getMessage(), e.getCause(),sb.toString()));
    }
}
