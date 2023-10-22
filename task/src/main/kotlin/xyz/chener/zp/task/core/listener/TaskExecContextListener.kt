package xyz.chener.zp.task.core.listener

import com.rabbitmq.client.Channel
import org.apache.shardingsphere.elasticjob.infra.listener.ShardingContexts
import org.apache.shardingsphere.elasticjob.lite.api.listener.AbstractDistributeOnceElasticJobListener
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.data.Stat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.connection.ChannelListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder
import xyz.chener.zp.common.utils.Json
import xyz.chener.zp.common.utils.chain.AbstractChainExecute
import xyz.chener.zp.common.utils.chain.ChainStarter
import xyz.chener.zp.task.config.TaskConfiguration
import xyz.chener.zp.task.core.ZookeeperProxy
import xyz.chener.zp.task.core.mq.MqConfig
import xyz.chener.zp.task.core.mq.MqConfig.Companion.DEFAULT_TOPIC_EXCHANGE
import xyz.chener.zp.task.core.notify.EmailNotify
import xyz.chener.zp.task.core.notify.MessageNotify
import xyz.chener.zp.task.core.notify.NotifyHeader
import xyz.chener.zp.task.core.notify.NotifyParam
import xyz.chener.zp.task.entity.TaskLog
import xyz.chener.zp.task.entity.TaskShardingExecLog
import xyz.chener.zp.task.entity.enums.TaskState
import xyz.chener.zp.task.service.TaskLogService
import xyz.chener.zp.task.service.TaskShardingExecLogService
import xyz.chener.zp.task.service.impl.TaskLogServiceImpl
import java.util.*
import kotlin.reflect.KMutableProperty1

class TaskExecContextListener : AbstractDistributeOnceElasticJobListener(0,0) {

    private val log : Logger = LoggerFactory.getLogger(TaskExecContextListener::class.java)

    companion object{
        const val CTX_PATH:String = "task-context"
    }

    override fun getType(): String {
        return TaskExecContextListener::class.java.name
    }


    override fun doBeforeJobExecutedAtLastStarted(shardingContexts: ShardingContexts?) {
        val uuid = UUID.randomUUID().toString()
        registerTaskUidToZk(uuid,shardingContexts?.jobName)

        val taskLogService = ApplicationContextHolder.getApplicationContext().getBean(TaskLogService::class.java)
        val taskLog = TaskLog().apply {
            this.taskId = shardingContexts?.taskId
            this.taskUid = uuid
            this.startTime = Date()
            this.jobName = shardingContexts?.jobName
            this.state = TaskState.RUNNING.int
        }
        taskLogService.save(taskLog)

        ChainStarter.start(getNotifyAllProcessor(), shardingContexts?.jobName?.let { NotifyParam(true,false, it) })
    }


    override fun doAfterJobExecutedAtLastCompleted(shardingContexts: ShardingContexts?) {
        val currentTaskUid = xyz.chener.zp.task.core.TaskUtils.getCurrentTaskUid(shardingContexts?.jobName)
        removeTaskUidFromZk(shardingContexts?.jobName)

        if (currentTaskUid.isNullOrEmpty()){
            log.error("任务上下文丢失: {},{}",shardingContexts?.jobName,shardingContexts?.taskId)
            return
        }

        // 更新任务状态
        val taskLogService = ApplicationContextHolder.getApplicationContext().getBean(TaskLogService::class.java)
        val tasklog:TaskLog? = taskLogService.ktQuery().eq(TaskLog::taskUid, currentTaskUid)
            .eq(TaskLog::jobName, shardingContexts?.jobName).one()
        tasklog?.let {
            val tses = ApplicationContextHolder.getApplicationContext().getBean(TaskShardingExecLogService::class.java)
            val execList = tses.ktQuery().eq(TaskShardingExecLog::taskUid, tasklog.taskUid)
                .select(TaskShardingExecLog::id, TaskShardingExecLog::state)
                .list()

            execList.forEach { ec->
                if (ec.state == TaskState.RUNNING.int) {
                    tses.ktUpdate().set(TaskShardingExecLog::state, TaskState.CRASH.int)
                        .eq(TaskShardingExecLog::id, ec.id).update()
                }
            }

            it.state = if (execList.map { it1->it1.state }.contains(TaskState.EXCEPTION.int))  TaskState.EXCEPTION.int else TaskState.FINISH.int
            it.endTime = Date()
            taskLogService.updateById(it)

            ChainStarter.start(getNotifyAllProcessor(), shardingContexts?.jobName?.let { it2 ->
                NotifyParam(false,it.state == 3, it2)
            })


            val rabbitTemplate  =ApplicationContextHolder.getApplicationContext().getBean(RabbitTemplate::class.java)
            rabbitTemplate.convertAndSend(DEFAULT_TOPIC_EXCHANGE,MqConfig.TASK_END_KEY,Json.json(it))
        }
    }


    private fun getNotifyAllProcessor():List<AbstractChainExecute> {
        val n1 = ApplicationContextHolder.getApplicationContext().getBean(NotifyHeader::class.java)
        val n2 = ApplicationContextHolder.getApplicationContext().getBean(EmailNotify::class.java)
        val n3 = ApplicationContextHolder.getApplicationContext().getBean(MessageNotify::class.java)
        return listOf(n1,n2,n3)
    }

    private fun registerTaskUidToZk(uuid:String,jobName:String?){
        val zk = ApplicationContextHolder.getApplicationContext().getBean(ZookeeperProxy::class.java)
        val cfg = ApplicationContextHolder.getApplicationContext().getBean(TaskConfiguration::class.java)
        val exists : Stat? = zk.exists("${cfg.taskCfg.vitureDir}/${jobName}/${CTX_PATH}", false)
        if (exists == null){
            zk.create("${cfg.taskCfg.vitureDir}/${jobName}/${CTX_PATH}",null,zk.getAcl(),CreateMode.PERSISTENT)
        }
        zk.setData("${cfg.taskCfg.vitureDir}/${jobName}/${CTX_PATH}", uuid.encodeToByteArray(),-1)
    }


    private fun removeTaskUidFromZk(jobName:String?){
        val zk = ApplicationContextHolder.getApplicationContext().getBean(ZookeeperProxy::class.java)
        val cfg = ApplicationContextHolder.getApplicationContext().getBean(TaskConfiguration::class.java)
        val exists : Stat? = zk.exists("${cfg.taskCfg.vitureDir}/${jobName}/${CTX_PATH}", false)
        if (exists != null){
            zk.setData("${cfg.taskCfg.vitureDir}/${jobName}/${CTX_PATH}",null, -1)
        }
    }

}