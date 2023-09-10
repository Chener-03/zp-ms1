package xyz.chener.zp.task.core

import xyz.chener.zp.task.core.error.DebuggerAttachTimeOutError

abstract class TaskAbstractExecute {

    @Volatile private var debuggerIsAttach: Boolean = false


    @Volatile var timeOut: Long = 0

    @Volatile var param:Any? = null

    constructor(){
        this.timeOut = System.currentTimeMillis() + 1000 * 60 * 10L
    }

    constructor(timeOut: Long) {
        this.timeOut = timeOut
    }

    var consoleLogOut:StringBuilder = StringBuilder()

    var consoleLogErr:StringBuilder = StringBuilder()


    protected fun waitDebuggerConnect(){
        while (!debuggerIsAttach && System.currentTimeMillis() < timeOut) {
            Thread.sleep(1000)
        }
        if (!debuggerIsAttach) {
            throw DebuggerAttachTimeOutError()
        }
    }

    protected fun redirectConsoleLog(){
        System.setOut(object : java.io.PrintStream(System.out) {
            override fun println(x: String?) {
                consoleLogOut.append(x).append("\n")
            }
        })

        System.setErr(object : java.io.PrintStream(System.err) {
            override fun println(x: String?) {
                consoleLogErr.append(x).append("\n")
            }
        })
    }


    abstract fun go()

    abstract fun getTaskHandler():TaskHandler


}