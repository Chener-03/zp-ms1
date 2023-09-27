package xyz.chener.zp.task.core

import org.apache.zookeeper.data.Stat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder
import xyz.chener.zp.common.utils.AssertUrils
import xyz.chener.zp.task.config.TaskConfiguration
import xyz.chener.zp.task.core.listener.TaskExecContextListener
import java.lang.RuntimeException

open class TaskUtils {

    companion object {

        private val log : Logger = LoggerFactory.getLogger(TaskUtils::class.java)

        fun getCurrentTaskUid(jobName:String?):String?{
            return try {
                val zk = ApplicationContextHolder.getApplicationContext().getBean(ZookeeperProxy::class.java)
                val cfg = ApplicationContextHolder.getApplicationContext().getBean(TaskConfiguration::class.java)
                val exists : Stat = zk.exists("${cfg.taskCfg.vitureDir}/${jobName}/${TaskExecContextListener.CTX_PATH}", false)
                val data = zk.getData("${cfg.taskCfg.vitureDir}/${jobName}/${TaskExecContextListener.CTX_PATH}", false, exists) as ByteArray
                data.decodeToString()
            }catch (e:Exception){
                null
            }
        }



    }

}