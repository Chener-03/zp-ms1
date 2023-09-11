package xyz.chener.zp.task.core.target

import xyz.chener.zp.task.core.entity.TaskData
import javax.management.MXBean


@MXBean
interface JarTaskMBean {

    fun getBatchNum(param: TaskData):Long

    fun handle(param: TaskData,batch:Long):String?

    fun toOutput():String

    fun toErrorOutput():String

    fun clearOut()

    fun shundown()

}