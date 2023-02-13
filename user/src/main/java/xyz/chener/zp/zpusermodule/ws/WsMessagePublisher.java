package xyz.chener.zp.zpusermodule.ws;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import xyz.chener.zp.zpusermodule.ws.mq.RemoteMessageConfig;
import xyz.chener.zp.zpusermodule.ws.mq.entity.NotifyMessage;

@Component
@Slf4j
public class WsMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public WsMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishWsUserMessage(NotifyMessage message) {
        try {
            String json = new ObjectMapper().writeValueAsString(message);
            rabbitTemplate.convertAndSend(RemoteMessageConfig.WS_MESSAGE_EXCHANG
                    ,RemoteMessageConfig.WS_USER_MESSAGE, json);
        }catch (Exception ex){
            log.warn("publishWsUserMessage error: {}", ex.getMessage());
        }
    }

}
