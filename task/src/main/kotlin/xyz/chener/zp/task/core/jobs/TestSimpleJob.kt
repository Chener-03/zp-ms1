package xyz.chener.zp.task.core.jobs

import org.apache.shardingsphere.elasticjob.api.ElasticJob
import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.lite.internal.setup.JobClassNameProvider
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import xyz.chener.zp.task.core.SimpleJobHandleProxy
import xyz.chener.zp.task.core.TaskUtils
import java.lang.RuntimeException


/**
 * 测试普通任务
 */
class TestSimpleJob : SimpleJobHandleProxy() {

    override fun executeJob(shardingContext: ShardingContext?) {
        println("任务${shardingContext?.shardingItem}开始 "+Thread.currentThread().name + "     ${TaskUtils.getCurrentTaskUid(shardingContext?.jobName)}")
        if (shardingContext?.shardingItem == 1){
            throw RuntimeException("测试异常")
        }
        getLogger()?.info("任务${shardingContext?.shardingItem}开始 hhh")
        Thread.sleep(1000*5)
        println("任务${shardingContext?.shardingItem}结束 "+Thread.currentThread().name)
    }
}