package xyz.chener.zp.task.core.error

import java.lang.RuntimeException

open class DebuggerAttachTimeOutError : RuntimeException("等待附加调试器超时,任务执行失败")


open class ExecuteOnlyOneError : RuntimeException("jar任务只能执行一个")
