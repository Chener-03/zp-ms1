package xyz.chener.zp.task.core

import org.redisson.api.RedissonClient
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder
import java.time.Duration
import java.util.Date

open class TaskLogger(private var taskUid: String,private var shardingItem: Int) {

    companion object {
        const val LOGS_KEY = "task-logs"
    }

    private val logs = StringBuilder()

    private val simpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private val redissonClient: RedissonClient = ApplicationContextHolder.getApplicationContext().getBean(RedissonClient::class.java)

    fun info(fmt:String?,vararg args:Any?){
        log("INFO",fmt,*args)
    }

    fun error(fmt:String?,vararg args:Any?){
        log("ERROR",fmt,*args)
    }


    private fun log(level:String , fmt:String?,vararg args:Any?){
        val str = "[${simpleDateFormat.format(Date())}] [$level]" + String.format(fmt?:"",*args) + "\n"
        logs.append(str)
        synchronousRedisLog(str)
    }

    private fun synchronousRedisLog(str:String){
        try {
            val list = redissonClient.getList<String>("${LOGS_KEY}:${taskUid}:${shardingItem}")
            list.expireAsync(Duration.ofDays(1))
            list.addAsync(str)
        }catch (_:Exception) {}
    }

    open fun clearRedisLog(){
        try {
            val list = redissonClient.getList<String>("${LOGS_KEY}:${taskUid}:${shardingItem}")
            list.delete()
        }catch (_:Exception) {}
    }

    fun getLogs():String{
        return logs.toString()
    }

}