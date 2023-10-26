package xyz.chener.zp.task.core.notify

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import xyz.chener.zp.common.utils.chain.AbstractChainExecute
import xyz.chener.zp.task.entity.NOTIFY_TIME
import xyz.chener.zp.task.entity.TaskInfo
import xyz.chener.zp.task.entity.TaskLog
import xyz.chener.zp.task.entity.TaskMetadata
import xyz.chener.zp.task.service.TaskInfoService


@Component
open class NotifyHeader : AbstractChainExecute(){

    @Autowired
    lateinit var taskInfoService: TaskInfoService

    override fun handle(param: Any?): Any? {
        if (param is NotifyParam){
            try {
                taskInfoService.ktQuery().eq(TaskInfo::jobName,param.jobName).one()?.let {

                    val taskMetadata = it.metadata!!
                    val nt = taskMetadata.taskNotify.notifyTime
                    if (param.isStart && NOTIFY_TIME.contains(NOTIFY_TIME.START.int,nt)){
                        return NotifyMetaData(param,param.taskLog,it,true)
                    }

                    if (param.isException && NOTIFY_TIME.contains(NOTIFY_TIME.EXCEPTION.int,nt)){
                        return NotifyMetaData(param,param.taskLog,it,false)
                    }

                    if (!param.isStart && NOTIFY_TIME.contains(NOTIFY_TIME.END.int,nt)){
                        return NotifyMetaData(param,param.taskLog,it,false)
                    }
                }
                return null
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return null
    }
}


data class NotifyParam(val isStart:Boolean,val isException:Boolean ,val jobName:String,val taskLog: TaskLog)

open class NotifyMetaData {
    var notifyParam:NotifyParam
    var taskLog:TaskLog
    var taskInfo:TaskInfo
    var start:Boolean

    constructor(notifyParam: NotifyParam, taskLog: TaskLog, taskInfo: TaskInfo,start:Boolean) {
        this.notifyParam = notifyParam
        this.taskLog = taskLog
        this.taskInfo = taskInfo
        this.start = start
    }
}

