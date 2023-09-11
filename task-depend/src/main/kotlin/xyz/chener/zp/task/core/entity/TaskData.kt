package xyz.chener.zp.task.core.entity

import java.io.Serializable

class TaskData :Serializable {


    // 启动输入的参数
    var userInputParam : String? = null


    // 任务链 上个任务执行结果 按照批次分为不同数组
    var lastTaskResult : Array<TaskResult> = arrayOf()

}

class TaskResult : Serializable{
    var batchNum:Long = 1
    var result:String? = null
}