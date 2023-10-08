package xyz.chener.zp.task.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.esotericsoftware.kryo.util.ObjectMap
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.shardingsphere.elasticjob.api.JobConfiguration
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cglib.core.ReflectUtils
import org.springframework.stereotype.Service
import xyz.chener.zp.common.utils.AssertUrils
import xyz.chener.zp.common.utils.ObjectUtils
import xyz.chener.zp.task.core.SimpleJobHandleProxy
import xyz.chener.zp.task.core.listener.TaskExecContextListener
import xyz.chener.zp.task.dao.TaskInfoDao
import xyz.chener.zp.task.entity.TaskInfo
import xyz.chener.zp.task.entity.TaskMetadata
import xyz.chener.zp.task.entity.enums.TaskType
import xyz.chener.zp.task.error.LoadTaskError
import xyz.chener.zp.task.error.TaskNotFoundError
import xyz.chener.zp.task.service.TaskInfoService


@Service
open class TaskInfoServiceImpl : ServiceImpl<TaskInfoDao, TaskInfo>(), TaskInfoService {

    @Autowired
    lateinit var dataSource: javax.sql.DataSource

    @Autowired
    lateinit var zookeeperRegistryCenter: ZookeeperRegistryCenter


    @Throws(RuntimeException::class)
    open fun getJobConfiguration(taskInfo: TaskInfo) : JobConfiguration {
        try {
            val taskMetadata = ObjectMapper().readValue(taskInfo.metadata, TaskMetadata::class.java)
            val builder = JobConfiguration.newBuilder(taskInfo.jobName, taskMetadata.jobShardingCount)
            builder.cron(taskMetadata.cron)
            builder.jobParameter(taskMetadata.jobParam)
            builder.monitorExecution(taskMetadata.monitor)
            builder.failover(taskMetadata.failover)
            builder.misfire(taskMetadata.misfire)
            builder.shardingItemParameters(taskMetadata.jobShardingParam)
            builder.timeZone(taskMetadata.timeZone)
            builder.description(taskMetadata.description)
            builder.jobErrorHandlerType("IGNORE")
            builder.jobListenerTypes(TaskExecContextListener::class.java.name)
            builder.addExtraConfigurations(TracingConfiguration("RDB",dataSource))
            return builder.build()
        }catch (e : Exception){
            throw RuntimeException("任务配置异常 :${e.message}")
        }
    }



    open fun loadTask(taskId:Long) {
        val taskInfo = this.ktQuery().eq(TaskInfo::id, taskId).one()
        AssertUrils.state(taskInfo != null, TaskNotFoundError::class.java)

        when (taskInfo!!.taskType){
            TaskType.SIMPLE.int -> {
                val instance = ObjectUtils.newInstance<SimpleJobHandleProxy>(taskInfo.taskHandle)
                AssertUrils.state(instance != null, LoadTaskError("找不到taskHandle:${taskInfo.taskHandle}"))
                val jobConfiguration = getJobConfiguration(taskInfo)
                ScheduleJobBootstrap(zookeeperRegistryCenter, instance, jobConfiguration).schedule()
            }

            else -> {
                throw LoadTaskError("未知的任务类型:${taskInfo.taskType}")
            }
        }
    }


}

