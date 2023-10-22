package xyz.chener.zp.task.core.mq;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MqListeners {


    @RabbitListener(queues = MqConfig.TASK_END_QUEUE)
    public void taskEnd(Message message, com.rabbitmq.client.Channel channel) {
        String payload = new String(message.getBody());
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
