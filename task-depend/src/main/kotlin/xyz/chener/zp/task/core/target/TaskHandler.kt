package xyz.chener.zp.task.core.target

import xyz.chener.zp.task.core.entity.TaskData

interface TaskHandler {

    fun handle(param: TaskData, batch:Long):String?


    fun getTaskBatchSize(param: TaskData):Long{
        return 1
    }

}