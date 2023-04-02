package xyz.chener.zp.common.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ThreadUtils {

    public static void runIgnoreException(RunnableWithException runnable){
        try {
            runnable.run();
        }catch (Throwable ignored){}
    }


    @FunctionalInterface
    public interface RunnableWithException{
        public abstract void run() throws Exception;
    }

}
