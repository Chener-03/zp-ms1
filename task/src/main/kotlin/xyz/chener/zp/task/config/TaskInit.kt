package xyz.chener.zp.task.config

import org.apache.shardingsphere.elasticjob.api.JobConfiguration
import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.ServerStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.ShardingStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import xyz.chener.zp.common.entity.PublicEnum
import xyz.chener.zp.task.core.SimpleJobHandleProxy
import xyz.chener.zp.task.core.ZookeeperProxy
import xyz.chener.zp.task.core.jobs.TestSimpleJob
import xyz.chener.zp.task.core.listener.TaskExecContextListener
import xyz.chener.zp.task.entity.TiggerType
import xyz.chener.zp.task.entity.TiggerTypeEnum
import xyz.chener.zp.task.service.TaskInfoService
import java.lang.reflect.Proxy
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

    @Autowired
    lateinit var redissonClient: RedissonClient

    override fun run(vararg args: String?) {



        if (1 == 1)
            return



        val jobConfiguration = JobConfiguration
            .newBuilder("test12", 2)
            .cron("0/10 * * * * ?")
            .jobParameter("123")
//            .shardingItemParameters("0=A")
            .addExtraConfigurations(TracingConfiguration("RDB",dataSource))
            .description("测试任务")
            .overwrite(true)
            .disabled(true)
            .misfire(false)
            .jobErrorHandlerType("IGNORE")
            .jobListenerTypes(TaskExecContextListener::class.java.name)
            .build()

        val currentTaskUid = xyz.chener.zp.task.core.TaskUtils.getCurrentTaskUid(jobConfiguration.jobName)

        /*        ScheduleJobBootstrap(
                    zookeeperRegistryCenter, "HTTP",
                    jobConfiguration
                    ).schedule()*/

        val job = TestSimpleJob()


        val jb = ScheduleJobBootstrap(zookeeperRegistryCenter, job, jobConfiguration)
        jb.schedule()

        /*Thread.ofVirtual().start{
            Thread.sleep(10 * 1000)
            jb.shutdown()
        }*/



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

        val jobBriefInfo = JobStatisticsAPIImpl(zookeeperRegistryCenter).getJobBriefInfo(jobConfiguration.jobName)

//        val shardingInfo = ShardingStatisticsAPIImpl(zookeeperRegistryCenter).getShardingInfo(jobConfiguration.jobName)


        println()
    }
}