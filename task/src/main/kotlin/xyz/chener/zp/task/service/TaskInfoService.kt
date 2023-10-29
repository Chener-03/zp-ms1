package xyz.chener.zp.task.service

import com.baomidou.mybatisplus.extension.service.IService
import com.github.pagehelper.PageInfo
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestBody
import xyz.chener.zp.common.entity.vo.PageParams
import xyz.chener.zp.task.entity.TaskInfo
import xyz.chener.zp.task.entity.TaskInfoVo


interface TaskInfoService : IService<TaskInfo> {
    fun getTaskLists(taskInfo: TaskInfoVo, pageParams: PageParams, username:String): PageInfo<TaskInfoVo>

    fun saveTaskInfo(taskInfo:TaskInfoVo):Boolean
}
