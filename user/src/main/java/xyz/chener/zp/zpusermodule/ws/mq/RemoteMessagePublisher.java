package xyz.chener.zp.zpusermodule.ws.mq;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RemoteMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public RemoteMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishWsUserMessage(Object message) {
        try {
            String json = new ObjectMapper().writeValueAsString(message);
            rabbitTemplate.convertAndSend(RemoteMessageConfig.WS_MESSAGE_EXCHANG
                    ,RemoteMessageConfig.WS_USER_MESSAGE, json);
        }catch (Exception ex){
            log.warn("publishWsUserMessage error: {}", ex.getMessage());
        }
    }

}
