package xyz.chener.zp.task.core.jobs

import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import java.lang.RuntimeException


/**
 * 测试普通任务
 */
class TestSimpleJob : SimpleJob {
    override fun execute(shardingContext: ShardingContext?) {
Thread.sleep(1000*10)
//        throw RuntimeException("测试异常")
    }
}