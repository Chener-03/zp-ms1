package xyz.chener.zp.task.entity

open class TiggerType {

    var tiggerTypeEnum:Int = TiggerTypeEnum.NONE.ordinal

    var taskEndDepId:Long? = null

    // 触发时机 NOTIFY_TIME::END | EXCEPTION
    var taskEndTirggetType:Int = 0
}


enum class TiggerTypeEnum {
    NONE,CRON,OTHER_TASK
}