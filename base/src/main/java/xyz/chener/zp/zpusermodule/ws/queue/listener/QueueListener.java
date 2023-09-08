package xyz.chener.zp.zpusermodule.ws.queue.listener;

import java.util.EventListener;

public interface QueueListener<T> extends EventListener {

    void onEvent(Object source);

}
