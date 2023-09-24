package xyz.chener.zp.task.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.chener.zp.task.core.ZookeeperProxy
import xyz.chener.zp.task.entity.ZooInstance
import xyz.chener.zp.task.service.InstanceService


@Service
open class InstanceServiceImpl : InstanceService {

    @Autowired
    lateinit var zookeeperProxy: ZookeeperProxy


    open override fun getOnlineInstance(): List<ZooInstance> {
        val list = zookeeperProxy.getChildren(zookeeperProxy.getRootDir(), false)
        return list.map {
            val data = zookeeperProxy.getData("${zookeeperProxy.getRootDir()}/$it", false, null)
            return@map ObjectMapper().readValue(data, ZooInstance::class.java)
        }
    }
}