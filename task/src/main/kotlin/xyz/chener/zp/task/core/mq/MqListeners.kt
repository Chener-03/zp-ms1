package xyz.chener.zp.task.core.mq

import com.rabbitmq.client.Channel
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import xyz.chener.zp.common.utils.Json
import xyz.chener.zp.common.utils.ThreadUtils
import xyz.chener.zp.task.entity.TaskLog


@Component
class MqListeners {
    companion object{
        private val log : Logger = LoggerFactory.getLogger(MqListeners::class.java)
    }



    @RabbitListener(queues = [MqConfig.TASK_END_QUEUE])
    fun taskEnd(message: Message, channel: Channel) {
        val payload = String(message.body)
        try {
            val taskLog = Json.obj(payload, TaskLog::class.java)
            log.info(payload)
            channel.basicAck(message.messageProperties.deliveryTag, false)
        } catch (e: Exception) {
            log.error("任务结束监听 消费失败: " + e.message)
            ThreadUtils.runIgnoreException {
                channel.basicNack(
                    message.messageProperties.deliveryTag, false, false
                )
            }
        }
    }
}

