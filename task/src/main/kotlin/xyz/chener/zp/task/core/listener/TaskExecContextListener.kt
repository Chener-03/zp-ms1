package xyz.chener.zp.task.core.listener

import org.apache.shardingsphere.elasticjob.infra.listener.ShardingContexts
import org.apache.shardingsphere.elasticjob.lite.api.listener.AbstractDistributeOnceElasticJobListener
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.data.Stat
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder
import xyz.chener.zp.task.config.TaskConfiguration
import xyz.chener.zp.task.core.ZookeeperProxy
import java.util.*

class TaskExecContextListener : AbstractDistributeOnceElasticJobListener(0,0) {

    companion object{
        val CTX_PATH:String = "task-context"
    }

    override fun getType(): String {
        return TaskExecContextListener::class.java.name
    }


    override fun doBeforeJobExecutedAtLastStarted(shardingContexts: ShardingContexts?) {
        val zk = ApplicationContextHolder.getApplicationContext().getBean(ZookeeperProxy::class.java)
        val cfg = ApplicationContextHolder.getApplicationContext().getBean(TaskConfiguration::class.java)

        val exists : Stat? = zk.exists("${cfg.taskCfg.vitureDir}/${CTX_PATH}", false)
        if (exists == null){
            zk.create("${cfg.taskCfg.vitureDir}/${CTX_PATH}",null,zk.getAcl(),CreateMode.PERSISTENT)
        }
        zk.setData("${cfg.taskCfg.vitureDir}/${CTX_PATH}", UUID.randomUUID().toString().encodeToByteArray(),-1)
        println()
    }

    override fun doAfterJobExecutedAtLastCompleted(shardingContexts: ShardingContexts?) {
        val zk = ApplicationContextHolder.getApplicationContext().getBean(ZookeeperProxy::class.java)
        val cfg = ApplicationContextHolder.getApplicationContext().getBean(TaskConfiguration::class.java)

        val exists : Stat? = zk.exists("${cfg.taskCfg.vitureDir}/${CTX_PATH}", false)
        if (exists != null){
            zk.setData("${cfg.taskCfg.vitureDir}/${CTX_PATH}",null,-1)
        }
    }



}