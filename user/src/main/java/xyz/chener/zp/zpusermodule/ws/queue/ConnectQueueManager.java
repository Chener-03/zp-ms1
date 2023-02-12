package xyz.chener.zp.zpusermodule.ws.queue;

import org.springframework.util.StringUtils;
import xyz.chener.zp.zpusermodule.ws.listener.ConnectExpListener;
import xyz.chener.zp.zpusermodule.ws.queue.entity.WsConnect;
import xyz.chener.zp.zpusermodule.ws.queue.listener.QueueListener;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ConnectQueueManager {

    private static volatile ConnectQueueManager instance = null;

    public static ConnectQueueManager getInstance() {
        if (instance == null) {
            synchronized (ConnectQueueManager.class) {
                if (instance == null) {
                    instance = new ConnectQueueManager();
                }
            }
        }
        return instance;
    }


    private final List<QueueListener<?>> listeners = new CopyOnWriteArrayList<>();

    private ExecutorService executor;

    private TreeSet<WsConnect> connectSet = new TreeSet<>();

    private ReentrantLock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private ConnectQueueManager() {
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("ConnectQueueManagerNotify-"+ UUID.randomUUID().toString().substring(0, 5));
            return thread;
        });
        Thread thread = new Thread(this::run);
        thread.setName("ConnectQueueManagerThread-"+ UUID.randomUUID().toString().substring(0, 5));
        thread.setDaemon(true);
        thread.start();
        this.addListener(new ConnectExpListener());
    }
    public void addListener(QueueListener<?> listener)
    {
        listeners.add(listener);
    }

    private void run(){
        while (!Thread.interrupted()){
            try {
                lock.lock();
                if (connectSet.isEmpty()){
                    condition.await();
                    continue;
                }
                WsConnect first = connectSet.first();
                if (first.getExp_time() > System.currentTimeMillis()){
                    condition.await(first.getExp_time() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    continue;
                }
                connectSet.removeIf(wsConnect -> wsConnect.equals(first));
                notifyListeners(first);
            }catch (Exception ignored) {}
            finally {
                lock.unlock();
            }
        }
    }

    public void addConnect(WsConnect connect)
    {
        try {
            lock.lock();
            try {
                connectSet.removeIf(wsConnect -> wsConnect.equals(connect));
            }catch (Exception ignored) {}
            connectSet.add(connect);
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public List<WsConnect> getValidConnection(){
        lock.lock();
        try {
            ArrayList<WsConnect> l = new ArrayList<>();
            connectSet.forEach(e->{
                if (StringUtils.hasText(e.getConnect_user()))
                    l.add(e);
            });
            return Collections.unmodifiableList(l);
        }finally {
            lock.unlock();
        }
    }

    public void renewal(String sessionId,String userName){
        addConnect(new WsConnect(sessionId,userName,WsConnect.nextMinute()));
    }

    public void removeConnect(String sessionId){
        lock.lock();
        try {
            connectSet.removeIf(wsConnect -> wsConnect.getConnect_uid().equals(sessionId));
        }finally {
            lock.unlock();
        }
    }

    private void notifyListeners(Object obj)
    {
        executor.submit(()->{
            for (QueueListener<?> listener : listeners) {
                if (((ParameterizedType) listener.getClass()
                        .getGenericInterfaces()[0])
                        .getActualTypeArguments()[0].getTypeName().equals(WsConnect.class.getName())) {
                    listener.onEvent(obj);
                }
            }
        });
    }

}
