package xyz.chener.zp.task.entity

import jakarta.validation.constraints.NotEmpty

// https://blog.csdn.net/muriyue6/article/details/105242252
open class TaskMetadata {

    var jobShardingCount:Int = 1

    var jobParam :String? = null

    var jobShardingParam:String? = null

    @NotEmpty(message = "任务Cron不能为空")
    var cron : String? = null

    var timeZone:String = "Asia/Shanghai"

    var record : Boolean = false

    var description: String? = null

    // 执行监视 关闭将无法启用failover等
    var monitor:Boolean = true

    // 错误转移
    var failover:Boolean = true

    // 错过时间后也执行
    var misfire:Boolean = false

    var taskNotify:TaskNotify = TaskNotify()

}