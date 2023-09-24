package xyz.chener.zp.task.controller

import com.alibaba.cloud.nacos.NacosDiscoveryProperties
import com.alibaba.cloud.nacos.NacosServiceManager
import com.alibaba.cloud.nacos.endpoint.NacosDiscoveryEndpoint
import com.alibaba.nacos.api.naming.NamingFactory
import org.apache.zookeeper.ZooKeeper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn
import xyz.chener.zp.common.entity.WriteList
import xyz.chener.zp.common.utils.NacosUtils
import xyz.chener.zp.task.entity.ZooInstance
import xyz.chener.zp.task.service.InstanceService


@RestController
@RequestMapping("/api/web/task")
@UnifiedReturn
@Validated
open class TaskListController {


    @Autowired
    lateinit var nacosUtils: NacosUtils

    @Autowired
    lateinit var instanceService: InstanceService

    @RequestMapping("/test")
    @WriteList
    fun test():String{
        val nsm = ApplicationContextHolder.getApplicationContext().getBean(NacosServiceManager::class.java)
        val allInstances = nacosUtils.manager.namingService.getAllInstances("zp-task-module", "zp")
        val b = ApplicationContextHolder.getApplicationContext().getBean(NacosDiscoveryProperties::class.java)


        return "1"
    }

    @GetMapping("/getOnlineInstance")
    @WriteList
    open fun getOnlineInstance():List<ZooInstance>{
        return instanceService.getOnlineInstance()
    }


}