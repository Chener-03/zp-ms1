package xyz.chener.zp.zpusermodule.ws.mq;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import xyz.chener.zp.zpusermodule.UserApplication;


@Configuration
public class RemoteMessageConfig {

    public static final String WS_MESSAGE_EXCHANG = "wsMessageExchange";
    public static final String WS_MESSAGE_QUEUE = "wsMessageQueue-"+ UserApplication.APP_UID.substring(0,8);
    public static final String WS_USER_MESSAGE = "wsUserMessage.#";
    public static final String SMS_USER_MESSAGE = "smsUserMessage.#";


    @Bean
    @Lazy(false)
    public Exchange wsMessageExchange(){
        return ExchangeBuilder.fanoutExchange(WS_MESSAGE_EXCHANG)
                .autoDelete()
                .durable(false).build();
    }

    @Bean
    @Lazy(false)
    public Queue wsMessageQueue(){
        return QueueBuilder.nonDurable(WS_MESSAGE_QUEUE)
                .autoDelete()
                .build();
    }

    @Bean
    @Lazy(false)
    public Binding wsMessageQueueBinding() {
        return BindingBuilder
                .bind(wsMessageQueue()).to(wsMessageExchange())
                .with(WS_USER_MESSAGE).noargs();
    }

}
