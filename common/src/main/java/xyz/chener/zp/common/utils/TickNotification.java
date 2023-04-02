package xyz.chener.zp.common.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TickNotification {

    public static final int TICK_TIME = 1000;

    private final List<Runnable> runnables = new CopyOnWriteArrayList<>();

    private static volatile TickNotification instance = null;

    private TickNotification() {
        Thread t = new Thread(()->{
            while (!Thread.interrupted()){
                try {
                    Thread.sleep(TICK_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tick();
            }
        });
        t.setDaemon(true);
        t.setName("TickNotification-Thread-Time:"+TICK_TIME);
        t.start();
    }

    private void tick() {
        runnables.forEach(e->{
            try {
                e.run();
            }catch (Throwable ignored){}
        });
    }

    public static TickNotification getInstance() {
        if (instance == null) {
            synchronized (TickNotification.class) {
                if (instance == null) {
                    instance = new TickNotification();
                }
            }
        }
        return instance;
    }

    public void addRunnable(Runnable runnable) {
        runnables.add(runnable);
    }

    public void removeRunnable(Runnable runnable) {
        runnables.remove(runnable);
    }

}
