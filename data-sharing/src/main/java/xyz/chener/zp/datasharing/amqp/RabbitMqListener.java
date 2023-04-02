package xyz.chener.zp.datasharing.amqp;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.ThreadUtils;
import xyz.chener.zp.common.utils.TickNotification;
import xyz.chener.zp.datasharing.connect.DBConnectorManager;

import java.io.IOException;

@Component
@Slf4j
public class RabbitMqListener implements ApplicationListener<ApplicationStartedEvent> {

    private final DBConnectorManager dbConnectorManager;
    private final RabbitTemplate rabbitTemplate;


    public RabbitMqListener(DBConnectorManager dbConnectorManager, RabbitTemplate rabbitTemplate) {
        this.dbConnectorManager = dbConnectorManager;
        this.rabbitTemplate = rabbitTemplate;
    }



    public void notify(String message) {
        if (ObjectUtils.nullSafeEquals(message,NotifyType.FLUSH_DATASOURCE)){
            log.info("message flush datasource");
            dbConnectorManager.flushDataSource();
        }
    }
    private Channel cn = null;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
         cn = rabbitTemplate.getConnectionFactory()
                .createConnection()
                .createChannel(false);
        try {
            cn.basicConsume(RabbitMQConfig.QUEUE,true,new DefaultConsumer(cn){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body);
                    RabbitMqListener.this.notify(message);
                }
            });
        } catch (IOException ignored) { }

        TickNotification.getInstance().addRunnable(()-> ThreadUtils.runIgnoreException(()->{
            if (cn != null && !cn.isOpen()){
                ThreadUtils.runIgnoreException(()-> cn.close());
                cn = rabbitTemplate.getConnectionFactory()
                        .createConnection()
                        .createChannel(false);
                try {
                    cn.basicConsume(RabbitMQConfig.QUEUE,true,new DefaultConsumer(cn){
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            String message = new String(body);
                            RabbitMqListener.this.notify(message);
                        }
                    });
                } catch (IOException ignored) { }
            }
        }));

    }
}
