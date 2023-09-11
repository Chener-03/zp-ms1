package xyz.chener.zp.task.core.op

import org.springframework.util.StringUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class Win32System : SystemOperate {
    override fun portIsAvailable(port: Int): Boolean {
        val processBuilder = ProcessBuilder("cmd.exe", "/c", "netstat -ano | find \"$port\"")
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val res = reader.readText()
        if (!process.waitFor(1L,TimeUnit.SECONDS)) {
            process.destroyForcibly()
            return false
        }
        reader.close()
        return !StringUtils.hasText(res)
    }

    override fun processExist(pid: Long): Boolean {
        val processBuilder = ProcessBuilder("cmd.exe", "/c", "tasklist /FI \"PID eq $pid\"")
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val res = reader.readText()
        if (!process.waitFor(1L,TimeUnit.SECONDS)) {
            process.destroyForcibly()
            return false
        }
        if (res.contains(".exe") && res.contains("$pid")) {
            return true
        }
        return false
    }

    override fun startProcess(cmd: String): Long {
        TODO("Not yet implemented")
    }
}