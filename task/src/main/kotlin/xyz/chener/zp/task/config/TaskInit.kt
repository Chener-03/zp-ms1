package xyz.chener.zp.task.config

import org.apache.shardingsphere.elasticjob.api.JobConfiguration
import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.settings.JobConfigurationAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration
import org.apache.zookeeper.AddWatchMode
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.data.Id
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import xyz.chener.zp.task.core.ZookeeperProxy
import javax.sql.DataSource


@Component
class TaskInit : CommandLineRunner {

    @Autowired
    lateinit var dataSource: DataSource


    @Autowired
    lateinit var zooKeeper: ZookeeperProxy

    override fun run(vararg args: String?) {



        zooKeeper.addWatch("/",object:Watcher{
            override fun process(event: WatchedEvent?) {
                println(event)
            }
        },AddWatchMode.PERSISTENT_RECURSIVE)



        if (1==1)return
        System.setProperty("elasticjob.preferred.network.ip","192.168.89.1")
        val zkConf = ZookeeperConfiguration("127.0.0.1:2181", "elastic-job").also {
//                it.digest = "zkadmin:Abcd1234"
        }
        val registryCenter = ZookeeperRegistryCenter(zkConf).also {
            it.init()
        }


        var job = object: SimpleJob {
            override fun execute(shardingContext: ShardingContext?) {

                println("132:" + shardingContext?.shardingItem)
            }
        }



        val jobConfiguration = JobConfiguration
            .newBuilder("test12", 3)
            .cron("0/5 * * * * ?")
            .jobParameter("123")
            .shardingItemParameters("0=A,1=B,2=C")
            .addExtraConfigurations(TracingConfiguration("RDB",dataSource))
            .overwrite(true)
            .disabled(true)
            .build()

//        ScheduleJobBootstrap(registryCenter,job,jobConfiguration).schedule()



        JobOperateAPIImpl(registryCenter).enable(jobConfiguration.jobName,"127.0.0.1")


        val af = JobConfigurationPOJO().also {
            it.shardingTotalCount = 3
            it.jobName = jobConfiguration.jobName
            it.cron = "0/5 * * * * ?"
        }
        JobConfigurationAPIImpl(registryCenter).updateJobConfiguration(af)

        JobStatisticsAPIImpl(registryCenter).allJobsBriefInfo
    }
}