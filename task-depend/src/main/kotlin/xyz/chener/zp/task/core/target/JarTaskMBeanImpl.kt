package xyz.chener.zp.task.core.target

import xyz.chener.zp.task.core.entity.TaskData
import javax.management.MXBean


@MXBean
class JarTaskMBeanImpl : JarTaskMBean {
    override fun getBatchNum(param: TaskData): Long {
        return JarTaskExecute.getHandler().getTaskBatchSize(param)
    }


    override fun handle(param: TaskData,batch:Long): String? {
        return JarTaskExecute.getHandler().handle(param,batch)
    }

    override fun toOutput(): String {
        return JarTaskExecute.output.toString()
    }

    override fun toErrorOutput(): String {
        return JarTaskExecute.errorOutput.toString()
    }

    override fun clearOut() {
        JarTaskExecute.output.setLength(0)
        JarTaskExecute.errorOutput.setLength(0)
    }

    override fun callShutDown() {
        try {
            JarTaskExecute.shundownHook.forEach { it.run() }
        }catch (_:Throwable){}
    }


    override fun end() {
        try {
            JarTaskExecute.lock.lock()
            JarTaskExecute.condition.signalAll()
        }finally {
            JarTaskExecute.lock.unlock()
        }
    }
}