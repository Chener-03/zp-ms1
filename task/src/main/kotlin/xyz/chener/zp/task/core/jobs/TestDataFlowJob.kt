package xyz.chener.zp.task.core.jobs

import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob

class TestDataFlowJob : DataflowJob<Any> {
    override fun fetchData(shardingContext: ShardingContext?): List<Any> {
        return emptyList()
    }

    override fun processData(shardingContext: ShardingContext?, data: List<Any>?) {

    }
}