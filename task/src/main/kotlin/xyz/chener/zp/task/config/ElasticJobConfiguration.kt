package xyz.chener.zp.task.config

import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import xyz.chener.zp.task.entity.ZooInstance


@Configuration
class ElasticJobConfiguration {


    @Bean
    @DependsOn("scopedTarget.zookeeperProxy")
    fun zookeeperRegistryCenter(cfg: TaskConfiguration) : ZookeeperRegistryCenter {
        cfg.taskCfg.registIp?.let{
            System.setProperty("elasticjob.preferred.network.ip",it)
        }

        val zkConf = ZookeeperConfiguration(cfg.taskCfg.address, cfg.taskCfg.vitureDir.run {
            if (this@run.startsWith("/")) {
                return@run this@run.substring(1)
            }else{
                return@run this@run
            }
        }).also {
            it.digest = cfg.taskCfg.digestACL
        }
        return ZookeeperRegistryCenter(zkConf).also {
            it.init()
        }
    }

}