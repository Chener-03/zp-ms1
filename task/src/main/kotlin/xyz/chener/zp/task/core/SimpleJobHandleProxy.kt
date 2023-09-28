package xyz.chener.zp.task.core

import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder
import xyz.chener.zp.task.entity.TaskShardingExecLog
import xyz.chener.zp.task.entity.ZooInstance
import xyz.chener.zp.task.entity.enums.TaskState
import xyz.chener.zp.task.service.TaskShardingExecLogService
import java.util.*

abstract class SimpleJobHandleProxy : SimpleJob {

    private val log : Logger = LoggerFactory.getLogger(SimpleJobHandleProxy::class.java)

    private var threadLogger:InheritableThreadLocal<TaskLogger> = InheritableThreadLocal()


    @Volatile
    private var zooInstance:ZooInstance? = null

    fun getLogger():TaskLogger?{
        return threadLogger.get()
    }



    final override fun execute(shardingContext: ShardingContext?) {
        val taskuid = TaskUtils.getCurrentTaskUid(shardingContext?.jobName)
        if (taskuid.isNullOrEmpty()){
            log.error("执行任务分片失败，找不到任务上下文: {},{}, item:{}",shardingContext?.jobName,shardingContext?.taskId,shardingContext?.shardingItem)
            return
        }

        threadLogger.set(TaskLogger(taskuid,shardingContext?.shardingItem?:0))

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
    }

    private fun recordTaskStart(taskuid:String,shardingContext: ShardingContext?):Boolean{
        return try {
            val taskShardingExecLogService = ApplicationContextHolder.getApplicationContext().getBean(TaskShardingExecLogService::class.java)
            if (zooInstance == null){
                zooInstance = ApplicationContextHolder.getApplicationContext().getBean(ZooInstance::class.java)
            }
            TaskShardingExecLog().run {
                this.startTime = Date()
                this.taskUid = taskuid
                this.state= TaskState.RUNNING.int
                this.shardingItem = shardingContext?.shardingItem?:0
                this.execServerAddress = zooInstance?.address
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

    abstract fun executeJob(shardingContext: ShardingContext?)

}