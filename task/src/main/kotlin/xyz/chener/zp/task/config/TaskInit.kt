package xyz.chener.zp.task.config

import org.apache.shardingsphere.elasticjob.api.JobConfiguration
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.ServerStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.ShardingStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Validator
import xyz.chener.zp.task.core.ZookeeperProxy
import xyz.chener.zp.task.core.jobs.TestSimpleJob
import xyz.chener.zp.task.core.listener.TaskExecContextListener
import xyz.chener.zp.task.entity.TaskInfo
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

    @Autowired
    lateinit var redissonClient: RedissonClient


    @Autowired
    @Lazy
    private val validator: Validator? = null

    override fun run(vararg args: String?) {
        val t = TaskInfo()

        var bindingResult =   BeanPropertyBindingResult(t,TaskInfo::class.java.name);
        validator?.validate(t, bindingResult);


//
//        if (1 == 1)
//            return



        val jobConfiguration = JobConfiguration
            .newBuilder("test12", 1)
            .cron("0/10 * * * * ?")
            .jobParameter("123")
//            .shardingItemParameters("0=A")
//            .addExtraConfigurations(TracingConfiguration("RDB",dataSource))
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



//        JobOperateAPIImpl(zookeeperRegistryCenter).enable(jobConfiguration.jobName,null)


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


//        JobOperateAPIImpl(zookeeperRegistryCenter).shutdown(jobConfiguration.jobName,null)

        Thread.ofVirtual().start {
            while (true){
                Thread.sleep(1000)
                try {
                    val jobBriefInfo = ShardingStatisticsAPIImpl(zookeeperRegistryCenter).getShardingInfo(jobConfiguration.jobName)
                    jobBriefInfo.forEach{
                        println("${it.instanceId}  ${it.item}   ${it.serverIp}   ${it.status.name}")
                    }
                }catch (_:Exception){
                }
            }
        }

//        val shardingInfo = ShardingStatisticsAPIImpl(zookeeperRegistryCenter).getShardingInfo(jobConfiguration.jobName)


        println()
    }
}