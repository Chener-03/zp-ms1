package xyz.chener.zp.task.core.mq


import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class MqConfig {

    companion object {
        const val TASK_END_QUEUE = "task-end-notify-queue"
        const val TASK_END_KEY = "task.end"
        const val DEFAULT_TOPIC_EXCHANGE = "amq.topic"
    }


    @Bean(name = ["rabbitListenerContainerFactory"])
    @ConditionalOnMissingBean(name = ["rabbitListenerContainerFactory"])
    @ConditionalOnProperty(
        prefix = "spring.rabbitmq.listener",
        name = ["type"],
        havingValue = "simple",
        matchIfMissing = true
    )
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory?): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL)
        return factory
    }


    @Bean
    open fun taskEndQueue(): Queue? {
        return QueueBuilder.durable(TASK_END_QUEUE)
            .build()
    }



    @Bean
    open fun defaultTopicExchange():Exchange?{
        return ExchangeBuilder.topicExchange("amq.topic")
            .durable(true)
            .build()
    }

    @Bean
    open fun bindTaskEndQueue():Binding?{
        return BindingBuilder.bind(taskEndQueue())
            .to(defaultTopicExchange())
            .with(TASK_END_KEY)
            .noargs()
    }


}