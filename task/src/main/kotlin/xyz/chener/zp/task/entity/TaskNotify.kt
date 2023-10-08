package xyz.chener.zp.task.entity

open class TaskNotify {

    var notifyTypes : Int = 0

    var notifyTime : Int = 0

    var znxUserId:List<Long> = ArrayList()

    var emails:List<String> = ArrayList()

}


enum class NOTIFY_TIME(val int:Int) {
    START(1),
    END(2),
    EXCEPTION(4);

    companion object {
        fun contains(int: Int, all: Int): Boolean {
            return (int and all) == int
        }
    }

    operator fun plus(other: NOTIFY_TIME): Int {
        return (this.int or other.int)
    }

    operator fun plus(other:Int):Int{
        return (this.int or other)
    }
}



enum class NOTIFY_TYPE(val int:Int) {
    ZNX(1),
    EMAIL(2),
    PHONE(4);

    companion object{
        fun contains(int: Int,all: Int):Boolean {
            return (int and all) == int
        }
    }

    operator fun plus(other: NOTIFY_TYPE): Int {
        return  (this.int or other.int)
    }


    operator fun plus(other:Int):Int{
        return (this.int or other)
    }

}
