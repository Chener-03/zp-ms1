package xyz.chener.zp.task.config

import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ElasticJobConfiguration {


    @Bean
    fun zookeeperRegistryCenter(cfg: TaskConfiguration) : ZookeeperRegistryCenter {
        cfg.taskCfg.registIp?.let{
            System.setProperty("elasticjob.preferred.network.ip",it)
        }

        val zkConf = ZookeeperConfiguration(cfg.taskCfg.address, cfg.taskCfg.vitureDir).also {
            it.digest = cfg.taskCfg.digestACL
        }
        return ZookeeperRegistryCenter(zkConf).also {
            it.init()
        }
    }

}