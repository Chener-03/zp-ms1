package xyz.chener.zp.task.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "zp.task")
open class TaskConfiguration {

    var zk : ZK = ZK()

    var taskCfg:TaskCfg = TaskCfg()

}

open class TaskCfg {
    var ip:String?=null
}

open class ZK {
    var address: String? = null
    var vitureDir: String = "/zp-task-dev"
    var digestACL: String? = null
    var connectTimeOut: Int = 5000
}