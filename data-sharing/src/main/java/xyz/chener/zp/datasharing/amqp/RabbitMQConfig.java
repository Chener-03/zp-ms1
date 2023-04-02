package xyz.chener.zp.datasharing.amqp;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import xyz.chener.zp.common.config.InfoRegistration;


@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "dataSharingExchange";
    public static String QUEUE = "dataSharingQueue-";
    public static final String MESSAGE = "";

    public RabbitMQConfig(){
        QUEUE += System.getProperty(InfoRegistration.APP_UID);
    }

    @Bean
    @Lazy(false)
    public Exchange datasharingExchange(){
        return ExchangeBuilder.fanoutExchange(EXCHANGE)
                .autoDelete()
                .durable(false).build();
    }

    @Bean
    @Lazy(false)
    public Queue datasharingMessageQueue(){
        return QueueBuilder.nonDurable(QUEUE)
                .autoDelete()
                .build();
    }

    @Bean
    @Lazy(false)
    public Binding datasharingQueueBinding() {
        return BindingBuilder
                .bind(datasharingMessageQueue()).to(datasharingExchange())
                .with(MESSAGE).noargs();
    }

}
