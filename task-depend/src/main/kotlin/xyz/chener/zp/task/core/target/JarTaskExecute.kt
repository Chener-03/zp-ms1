package xyz.chener.zp.task.core.target

import xyz.chener.zp.task.core.error.TaskHandlerNotNull
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.Exception
import java.lang.management.ManagementFactory
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import javax.management.MBeanServer
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import kotlin.system.exitProcess


object JarTaskExecute{

    const val REG_PATH = "xyz.chener.zp.task.core.target:type=JarTaskMBean";

    const val WAIT_CONNECT_TIMEOUT = 1000 * 60 * 5L

    internal var lock:ReentrantLock = ReentrantLock()

    internal var condition : Condition = lock.newCondition()

    private var taskHandler:TaskHandler? = null

    internal var output:StringBuffer = StringBuffer()

    internal var errorOutput:StringBuffer = StringBuffer()

    internal var shundownHook:List<Runnable> = ArrayList()



    init {
        redirectSystemOut()
        prepareMBean()
        timeOutKill()
    }

    private fun timeOutKill(){
        Thread {
            try {
                while (true){
                    Thread.sleep(WAIT_CONNECT_TIMEOUT)
                    if (getMxbeanConnectCount() == 0){
                        println("超时未连接，自动退出")
                        exitProcess(0)
                    }
                }
            }catch (_:Throwable){}
        }.let {
            it.isDaemon = true
            it.start()
        }
    }

    private fun getMxbeanConnectCount() : Int {
        try {
            val serverConnection = ManagementFactory.getPlatformMBeanServer() as MBeanServerConnection
            return serverConnection.mBeanCount
        }catch (ignored:Exception){}
        return 0
    }


    fun getHandler():TaskHandler{
        if (taskHandler == null){
            throw TaskHandlerNotNull()
        }
        return taskHandler!!
    }


    @JvmStatic
    fun waitTaskFinish(taskHandler:TaskHandler){
        this.taskHandler = taskHandler
        try {
            lock.lock()
            condition.await()
        }finally {
            lock.unlock()
        }
    }

    @JvmStatic
    fun addShunDownHook(runnable: Runnable){
        shundownHook += runnable
    }

    private fun prepareMBean(){
        val mbs = ManagementFactory.getPlatformMBeanServer()
        val name = ObjectName(REG_PATH)
        val mbean: JarTaskMBean = JarTaskMBeanImpl()
        mbs.registerMBean(mbean, name)
        println("MBean registered!")
    }

    private fun redirectSystemOut(){
        System.setOut(object :java.io.PrintStream(System.out){
            override fun println(x: String?) {
                output.append(x).append("\n")
            }
            override fun print(x: String?) {
                output.append(x).append("\n")
            }
        })

        System.setErr(object :java.io.PrintStream(System.err){
            override fun println(x: String?) {
                errorOutput.append(x).append("\n")
            }

            override fun print(x: String?) {
                errorOutput.append(x).append("\n")
            }
        })

    }

}