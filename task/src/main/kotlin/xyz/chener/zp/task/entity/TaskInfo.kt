package xyz.chener.zp.task.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.extension.activerecord.Model


open class TaskInfo : Model<TaskInfo?>() {

    @TableId(type = IdType.ASSIGN_ID)
    var id: Long? = null

    var taskName: String? = null

    var orgId: Long? = null

    var createUserId: Long? = null

    //任务类型 handle dataflow  jar http等 或者其它..
    var taskType: String? = null

    //执行器
    var taskHandle: String? = null

    //是否启用
    var enable: Int? = null

    //触发类型  cron   手动  其它任务调用
    var tiggerType: String? = null

    //元数据JSON  存储任务数据 如cron 参数等
    var metadata: String? = null

}
