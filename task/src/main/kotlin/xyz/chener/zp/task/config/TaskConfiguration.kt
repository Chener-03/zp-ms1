package xyz.chener.zp.task.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope


@ConfigurationProperties(prefix = "zp.task")
@RefreshScope
open class TaskConfiguration {

    var zk : ZK = ZK()

    var taskCfg:TaskCfg = TaskCfg()

}

open class TaskCfg {
    var address:String="127.0.0.1:2181"
    var vitureDir:String = "/zp-elastic-job-dev"
    var registIp:String? = null
    var digestACL: String? = null
}

open class ZK {
    var address: String ="127.0.0.1:2181"
    var vitureDir: String = "/zp-task-dev"
    var digestACL: String? = null
    var connectTimeOut: Int = 5000
}