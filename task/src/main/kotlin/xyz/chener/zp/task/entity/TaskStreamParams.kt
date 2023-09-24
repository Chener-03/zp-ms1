package xyz.chener.zp.task.entity

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.UUID


/**
 * 包装任务运行中的参数
 */
open class TaskStreamParams {

    var param:String? = null

    var concurrentTaskExecUid:String = UUID.randomUUID().toString().replace("-","")

    constructor(){}

    constructor(param: String?) {
        this.param = param
    }

    companion object {
        @JvmStatic
        fun of(json: String?):TaskStreamParams?{
            if (json.isNullOrEmpty())
                return TaskStreamParams()


            ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(json,TaskStreamParams::class.java).also {
                    return@of it
                }
        }
    }

}