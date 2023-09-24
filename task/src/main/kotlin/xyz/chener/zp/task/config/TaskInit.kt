package xyz.chener.zp.task.config

import org.apache.shardingsphere.elasticjob.api.JobConfiguration
import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.error.handler.JobErrorHandler
import org.apache.shardingsphere.elasticjob.error.handler.JobErrorHandlerPropertiesValidator
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.settings.JobConfigurationAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.ServerStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration
import org.apache.zookeeper.AddWatchMode
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.quartz.JobStoreType
import org.springframework.stereotype.Component
import xyz.chener.zp.task.core.ZookeeperProxy
import xyz.chener.zp.task.core.jobs.TestSimpleJob
import xyz.chener.zp.task.core.listener.LoggerAppendListener
import xyz.chener.zp.task.service.TaskInfoService
import javax.sql.DataSource


@Component
class TaskInit : CommandLineRunner {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var zooKeeper: ZookeeperProxy

    @Autowired
    lateinit var taskInfoService: TaskInfoService

    @Autowired
    lateinit var zookeeperRegistryCenter: ZookeeperRegistryCenter

    override fun run(vararg args: String?) {



        val jobConfiguration = JobConfiguration
            .newBuilder("test12", 3)
            .cron("0/10 * * * * ?")
            .jobParameter("123")
            .shardingItemParameters("0=A,1=B,2=C")
            .addExtraConfigurations(TracingConfiguration("RDB",dataSource))
            .description("测试任务")
            .overwrite(true)
            .disabled(true)
            .jobErrorHandlerType("LOG")
            .jobListenerTypes(LoggerAppendListener::class.java.name)
            .build()

/*        ScheduleJobBootstrap(
            zookeeperRegistryCenter, "HTTP",
            jobConfiguration
            ).schedule()*/

        val job = TestSimpleJob()
        ScheduleJobBootstrap(zookeeperRegistryCenter, job, jobConfiguration).schedule()



        JobOperateAPIImpl(zookeeperRegistryCenter).enable(jobConfiguration.jobName,null)


/*
        val af = JobConfigurationPOJO().also {
            it.shardingTotalCount = 3
            it.jobName = jobConfiguration.jobName
            it.cron = "0/5 * * * * ?"
        }
        JobConfigurationAPIImpl(zookeeperRegistryCenter).updateJobConfiguration(af)
*/

        JobStatisticsAPIImpl(zookeeperRegistryCenter).allJobsBriefInfo
        val serversTotalCount = ServerStatisticsAPIImpl(zookeeperRegistryCenter).serversTotalCount
        val allServersBriefInfo = ServerStatisticsAPIImpl(zookeeperRegistryCenter).allServersBriefInfo





        println()
    }
}