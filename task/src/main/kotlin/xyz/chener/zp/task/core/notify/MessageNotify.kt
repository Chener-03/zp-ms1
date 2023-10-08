package xyz.chener.zp.task.core.notify

import org.springframework.stereotype.Component
import xyz.chener.zp.common.utils.chain.AbstractChainExecute
import xyz.chener.zp.task.entity.NOTIFY_TYPE
import xyz.chener.zp.task.entity.TaskNotify


@Component
class MessageNotify : AbstractChainExecute() {
    override fun handle(param: Any?): Any? {
        if (param is TaskNotify && NOTIFY_TYPE.contains(NOTIFY_TYPE.ZNX.int,param.notifyTypes)){
            println("发送ZNX通知: ${param.znxUserId}")
        }
        return param
    }
}