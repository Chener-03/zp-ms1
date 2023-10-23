package xyz.chener.zp.task.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.extension.activerecord.Model
import java.util.*


open class TaskLog : Model<TaskLog?>() {

    @TableId(type = IdType.AUTO)
    var id: Int? = null

    var taskUid: String? = null

    var startTime: Date? = null

    var endTime: Date? = null

    //1 进行中    2 已完成   3 部分抛异常    4程序中断无法完成
    var state: Int? = null

    var errorMessage: String? = null

    var jobName: String? = null

    //对应elasticjob日志表的taskid
    var taskId: String? = null

    var resultString: String? = null
}

