package xyz.chener.zp.task.core.jobs

import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob


/**
 * 测试分片任务
 */
open class TestShardingSimpleJob : SimpleJob {
    override fun execute(shardingContext: ShardingContext?) {

    }
}