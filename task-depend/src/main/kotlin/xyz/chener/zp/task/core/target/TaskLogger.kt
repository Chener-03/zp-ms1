package xyz.chener.zp.task.core.target

class TaskLogger {

    companion object {

        @JvmStatic
        fun logInfo(format: String, vararg args: Any?) {
            JarTaskExecute.output.append(String.format(format,args)).append("\n")
        }

        @JvmStatic
        fun logError(format: String, vararg args: Any?) {
            JarTaskExecute.errorOutput.append(String.format(format,args)).append("\n")
        }

    }

    fun test(a:String){

    }

}