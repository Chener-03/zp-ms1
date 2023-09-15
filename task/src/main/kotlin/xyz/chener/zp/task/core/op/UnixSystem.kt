package xyz.chener.zp.task.core.op

import org.springframework.util.StringUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class UnixSystem : SystemOperate {
    override fun portIsAvailable(port: Int): Boolean {
        val processBuilder = ProcessBuilder("/bin/bash", "-c", "lsof -i:$port")
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val res = reader.readText()
        if (!process.waitFor(1L, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            return false
        }
        reader.close()
        return !StringUtils.hasText(res)
    }

    override fun processExist(pid: Long): Boolean {
        val processBuilder = ProcessBuilder("/bin/bash", "-c", "ps -p $pid")
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val res = reader.readText()
        if (!process.waitFor(1L, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            return false
        }
        reader.close()
        return res.contains(" $pid ")
    }

    override fun startProcess(cmd: String, envPath: String): Long {
        TODO("Not yet implemented")
    }
}