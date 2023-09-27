package xyz.chener.zp.task.entity.enums

enum class TaskState(val int: Int,val typeName:String) {
    RUNNING(1,"进行中"),
    FINISH(2,"完成"),
    EXCEPTION(3,"异常"),

}