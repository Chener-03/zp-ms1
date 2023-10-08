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
//        this.execute(shardingContext)
        println("任务${shardingContext?.shardingItem}开始 "+Thread.currentThread().name + "     ${TaskUtils.getCurrentTaskUid(shardingContext?.jobName)}")
        if (shardingContext?.shardingItem == 1){
//            throw RuntimeException("测试异常")

            Thread.sleep(1000*1)
        }else{
            for (i in 1..5){
                if (shouldEnd()) {
                    break
                }
                Thread.sleep(1000*1)
                println("任务${shardingContext?.shardingItem}---${i}执行中")
                getLogger()?.info("任务${shardingContext?.shardingItem}---${i}执行中")
            }
        }
        getLogger()?.info("任务${shardingContext?.shardingItem}开始 hhh")

        println("任务${shardingContext?.shardingItem}结束 "+Thread.currentThread().name)
    }
}