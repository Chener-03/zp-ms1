package xyz.chener.zp.task.core.op

import java.util.*


interface SystemOperate {

    fun portIsAvailable(port:Int):Boolean

    fun processExist(pid:Long):Boolean

    fun startProcess(cmd:String,envPath:String):Long

    companion object{
        fun getSystemOperate():SystemOperate{
            val os = System.getProperty("os.name")
            return if (os.lowercase(Locale.getDefault()).startsWith("win")) {
                Win32System()
            } else {
                UnixSystem()
            }
        }
    }

}