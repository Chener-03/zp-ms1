package xyz.chener.zp.task.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.data.Id
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.chener.zp.task.core.ZookeeperProxy
import xyz.chener.zp.task.entity.ZooInstance
import java.net.InetAddress
import java.util.*


@Configuration
class ZooInstanceRegister {

    private val log : Logger = LoggerFactory.getLogger(ZooInstanceRegister::class.java)

    @Value("\${server.port}")
    var port : Int = 0

    @Bean
    fun zookeeperInstance(taskConfiguration: TaskConfiguration) : ZookeeperProxy {
        return ZookeeperProxy(taskConfiguration.zk.address,taskConfiguration.zk.connectTimeOut,object : Watcher{
            override fun process(event: WatchedEvent?) {

            }
        },taskConfiguration)
    }


    @Bean
    fun zooInstance(zk:ZookeeperProxy,taskConfiguration: TaskConfiguration):ZooInstance {
        val ip = Optional.ofNullable(taskConfiguration.taskCfg.ip).orElseGet {
            val addr = InetAddress.getLocalHost()
            return@orElseGet "${addr.hostAddress}:$port"
        }
        val zooInstance = ZooInstance(
            ip,
            System.currentTimeMillis(),
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().totalMemory()
        )
        val path = zk.create("${zk.getRootDir()}/${zooInstance.address}", ObjectMapper().writeValueAsBytes(zooInstance), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)
        log.info("注册实例:{}",path)
        return zooInstance
    }


}