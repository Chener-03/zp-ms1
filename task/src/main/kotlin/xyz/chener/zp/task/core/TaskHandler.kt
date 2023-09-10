package xyz.chener.zp.task.core

interface TaskHandler {

    fun handle(param: Any?,batch:Long):Any?


    fun getTaskBatchSize(param: Any?):Long{
        return 1
    }

}