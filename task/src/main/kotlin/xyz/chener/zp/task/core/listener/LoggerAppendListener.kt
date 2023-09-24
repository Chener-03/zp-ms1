package xyz.chener.zp.task.core.listener

import org.apache.shardingsphere.elasticjob.infra.listener.ShardingContexts
import org.apache.shardingsphere.elasticjob.lite.api.listener.AbstractDistributeOnceElasticJobListener
import org.apache.shardingsphere.elasticjob.lite.internal.guarantee.GuaranteeService

class LoggerAppendListener : AbstractDistributeOnceElasticJobListener(0,0) {

    override fun getType(): String {
        return LoggerAppendListener::class.java.name
    }


    override fun doBeforeJobExecutedAtLastStarted(shardingContexts: ShardingContexts?) {
        println()
    }

    override fun doAfterJobExecutedAtLastCompleted(shardingContexts: ShardingContexts?) {
        println()
    }



}