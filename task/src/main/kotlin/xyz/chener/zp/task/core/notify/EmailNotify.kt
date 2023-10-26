package xyz.chener.zp.task.core.notify

import org.springframework.stereotype.Component
import xyz.chener.zp.common.utils.Json
import xyz.chener.zp.common.utils.chain.AbstractChainExecute
import xyz.chener.zp.task.entity.NOTIFY_TYPE
import xyz.chener.zp.task.entity.TaskNotify


@Component
open class EmailNotify : AbstractChainExecute() {
    override fun handle(param: Any?): Any? {
        if (param is NotifyMetaData && NOTIFY_TYPE.contains(NOTIFY_TYPE.EMAIL.int,param.taskInfo.metadata!!.taskNotify.notifyTypes)){
            println("发送邮件通知: ${Json.json(param)}")
        }
        return param
    }
}