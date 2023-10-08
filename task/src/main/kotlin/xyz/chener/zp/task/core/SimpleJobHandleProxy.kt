package xyz.chener.zp.task.core

import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import org.apache.zookeeper.data.Stat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder
import xyz.chener.zp.task.config.ElasticJobConfiguration
import xyz.chener.zp.task.config.TaskConfiguration
import xyz.chener.zp.task.entity.TaskShardingExecLog
import xyz.chener.zp.task.entity.ZooInstance
import xyz.chener.zp.task.entity.enums.TaskState
import xyz.chener.zp.task.service.TaskShardingExecLogService
import java.util.*

abstract class SimpleJobHandleProxy : SimpleJob {

    private val log : Logger = LoggerFactory.getLogger(SimpleJobHandleProxy::class.java)

    private var threadLogger:InheritableThreadLocal<TaskLogger> = InheritableThreadLocal()

    private var threadShardingContext:InheritableThreadLocal<ShardingContext?> = InheritableThreadLocal()


    @Volatile
    private var zooInstance:ZooInstance? = null

    fun getLogger():TaskLogger?{
        return threadLogger.get()
    }

    fun shouldEnd():Boolean{
        return try {
            val shardingContext = threadShardingContext.get()
            val zk = ApplicationContextHolder.getApplicationContext().getBean(ZookeeperProxy::class.java)
            val tc = ApplicationContextHolder.getApplicationContext().getBean(TaskConfiguration::class.java)
            val stat : Stat? = zk.exists(
                "${tc.taskCfg.vitureDir}/${shardingContext?.jobName}/instances/${getZooInstance().jobInstanceId}",
                false
            )
            if (stat == null) {
                getLogger()?.info("[外部终止信号]任务实例已被移除，任务分片执行终止: {%s},{%s}, item:{%s}",shardingContext?.jobName,shardingContext?.taskId,shardingContext?.shardingItem)
                true
            }else{
                return false
            }
        }catch (e:Exception) {
            return true
        }
    }


    final override fun execute(shardingContext: ShardingContext?) {
        if (checkSelfCall()){
            log.error("检测到任务自调用: {},{}, item:{}",shardingContext?.jobName,shardingContext?.taskId,shardingContext?.shardingItem)
            return
        }

        val taskuid = TaskUtils.getCurrentTaskUid(shardingContext?.jobName)
        if (taskuid.isNullOrEmpty()){
            log.error("执行任务分片失败，找不到任务上下文: {},{}, item:{}",shardingContext?.jobName,shardingContext?.taskId,shardingContext?.shardingItem)
            return
        }

        threadLogger.set(TaskLogger(taskuid,shardingContext?.shardingItem?:0))
        threadShardingContext.set(shardingContext)

        if (!recordTaskStart(taskuid,shardingContext)){
            return
        }

        try {
            executeJob(shardingContext)
            recordTaskEnd(taskuid,shardingContext,null)
        } catch (e: Throwable) {
            recordTaskEnd(taskuid,shardingContext,e)
        }

        threadLogger.get()?.clearRedisLog()
        threadLogger.remove()
        threadShardingContext.remove()
    }

    private fun recordTaskStart(taskuid:String,shardingContext: ShardingContext?):Boolean{
        return try {
            val taskShardingExecLogService = ApplicationContextHolder.getApplicationContext().getBean(TaskShardingExecLogService::class.java)
            TaskShardingExecLog().run {
                this.startTime = Date()
                this.taskUid = taskuid
                this.state= TaskState.RUNNING.int
                this.shardingItem = shardingContext?.shardingItem?:0
                this.execServerAddress = getZooInstance().address
                taskShardingExecLogService.save(this)
            }
            true
        }catch (e:Exception){
            log.error("记录分片任务开始日志失败,任务分片执行终止: {}  {},{},{}",e.message,shardingContext?.jobName,shardingContext?.taskId,shardingContext?.shardingItem)
            false
        }
    }

    private fun recordTaskEnd(taskuid:String,shardingContext: ShardingContext?,err:Throwable?){
        try {
            val taskShardingExecLogService = ApplicationContextHolder.getApplicationContext().getBean(TaskShardingExecLogService::class.java)
            val shardingExecLog = taskShardingExecLogService.ktQuery().eq(TaskShardingExecLog::taskUid, taskuid)
                .eq(TaskShardingExecLog::shardingItem, shardingContext?.shardingItem ?: 0).one()
            shardingExecLog?.run {
                this.endTime = Date()
                this.state = if (err == null) TaskState.FINISH.int else TaskState.EXCEPTION.int
                this.errMessage = err?.message
                this.logs = threadLogger.get()?.getLogs()
                taskShardingExecLogService.updateById(this)
            }
        } catch (e:Exception) {
            log.error("记录分片任务结束日志失败: {}  {},{},{}",e.message,shardingContext?.jobName,shardingContext?.taskId,shardingContext?.shardingItem)
        }
    }


    private fun checkSelfCall():Boolean {
        var count = 0
        Thread.currentThread().stackTrace.forEachIndexed { index, stackTraceElement ->
            if (stackTraceElement.className == SimpleJobHandleProxy::class.java.name && stackTraceElement.methodName == SimpleJobHandleProxy::execute.name){
                count ++
            }
            if (index > 20)
                return@forEachIndexed
        }
        return count > 1
    }


    abstract fun executeJob(shardingContext: ShardingContext?)

    private fun getZooInstance():ZooInstance{
        if (zooInstance == null){
            zooInstance = ApplicationContextHolder.getApplicationContext().getBean(ZooInstance::class.java)
        }
        return zooInstance!!
    }
}