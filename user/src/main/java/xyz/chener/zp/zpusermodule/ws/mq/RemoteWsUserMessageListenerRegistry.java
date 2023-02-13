package xyz.chener.zp.zpusermodule.ws.mq;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import xyz.chener.zp.zpusermodule.ws.mq.listener.NotifyMessageListener;
import xyz.chener.zp.zpusermodule.ws.mq.listener.MqListener;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RemoteWsUserMessageListenerRegistry implements ApplicationListener<ApplicationStartedEvent> {


    private final RabbitTemplate rabbitTemplate;

    private final List<MqListener> listeners = new CopyOnWriteArrayList<>();

    public RemoteWsUserMessageListenerRegistry(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            Channel cn = rabbitTemplate.getConnectionFactory()
                    .createConnection()
                    .createChannel(false);
            cn.basicConsume(RemoteMessageConfig.WS_MESSAGE_QUEUE,true,new MqWsMessageListenerDispatch(cn,listeners));
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }
        this.listeners.add(new NotifyMessageListener());
    }

    public void addListener(MqListener listener){
        this.listeners.add(listener);
    }

    @Slf4j
    public static class MqWsMessageListenerDispatch extends DefaultConsumer{
        private final List<MqListener> listeners;
        public MqWsMessageListenerDispatch(Channel channel,List<MqListener> listeners) {
            super(channel);
            this.listeners = listeners;
        }
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            listeners.forEach(e->{
                try {
                    Type type = ((ParameterizedType) e.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
                    String json = new String(body);
                    ObjectMapper om = new ObjectMapper();
                    Object o = om.readValue(json, TypeFactory.defaultInstance().constructFromCanonical(type.getTypeName()));
                    e.onMessage(o);
                }catch (Exception ex){
                    log.warn("handle message error",ex);
                }
            });
        }
    }


}
