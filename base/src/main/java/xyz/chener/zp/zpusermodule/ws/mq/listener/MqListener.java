package xyz.chener.zp.zpusermodule.ws.mq.listener;

public interface MqListener<T> {

    default void onMessage(Object message) {
        System.out.println(message);
    }

}
