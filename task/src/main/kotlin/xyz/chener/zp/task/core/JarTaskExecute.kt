package xyz.chener.zp.task.core

import xyz.chener.zp.task.core.error.ExecuteOnlyOneError
import java.util.concurrent.atomic.AtomicBoolean

class JarTaskExecute : TaskAbstractExecute {

    companion object {
        private val start: AtomicBoolean = AtomicBoolean(false)
    }

    private var taskHandler: TaskHandler

    constructor(taskHandler: TaskHandler) : super() {
        this.taskHandler = taskHandler
    }

    constructor(connectTimeout: Long, taskHandler: TaskHandler) : super(connectTimeout) {
        this.taskHandler = taskHandler
    }


    override fun go() {
        if (!start.compareAndSet(false, true)) {
            throw ExecuteOnlyOneError()
        }
        waitDebuggerConnect()
        redirectConsoleLog()


    }

    override fun getTaskHandler(): TaskHandler {
        return taskHandler
    }


}