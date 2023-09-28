package xyz.chener.zp.task.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.extension.activerecord.Model
import java.util.*


open class TaskShardingExecLog : Model<TaskShardingExecLog?>() {

    @TableId(type = IdType.AUTO)
    var id: Long? = null

    var taskUid: String? = null

    var shardingItem: Int? = null

    var execServerAddress:String? = null

    var logs: String? = null

    //1进行中  2完成  3抛异常
    var state: Int? = null
    var errMessage: String? = null
    var startTime: Date? = null
    var endTime: Date? = null
}


