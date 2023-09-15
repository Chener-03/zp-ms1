package xyz.chener.zp.task.config

import org.apache.shardingsphere.elasticjob.api.JobConfiguration
import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.settings.JobConfigurationAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import javax.sql.DataSource


@Component
class TaskInit : CommandLineRunner {

    @Autowired
    lateinit var dataSource: DataSource


    override fun run(vararg args: String?) {
        val zkConf = ZookeeperConfiguration("127.0.0.1:2181", "elastic-job").also {
//                it.digest = "zkadmin:Abcd1234"
        }
        val registryCenter = ZookeeperRegistryCenter(zkConf).also {
            it.init()
        }


        var job = object: SimpleJob {
            override fun execute(shardingContext: ShardingContext?) {
                println()
            }
        }



        val jobConfiguration = JobConfiguration
            .newBuilder("test12", 1).cron("0/5 * * * * ?")
            .jobParameter("123").addExtraConfigurations(TracingConfiguration("mysql",dataSource))
            .build()

        ScheduleJobBootstrap(registryCenter,job,jobConfiguration).schedule()

        JobOperateAPIImpl(registryCenter).enable(jobConfiguration.jobName,"192.168.145.1")

        JobConfigurationAPIImpl(registryCenter).getJobConfiguration(jobConfiguration.jobName)

        JobStatisticsAPIImpl(registryCenter).allJobsBriefInfo
    }
}