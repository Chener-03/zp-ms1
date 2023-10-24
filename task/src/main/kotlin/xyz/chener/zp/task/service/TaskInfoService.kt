package xyz.chener.zp.task.service

import com.baomidou.mybatisplus.extension.service.IService
import com.github.pagehelper.PageInfo
import org.springframework.web.bind.annotation.ModelAttribute
import xyz.chener.zp.common.entity.vo.PageParams
import xyz.chener.zp.task.entity.TaskInfo


interface TaskInfoService : IService<TaskInfo> {
    fun getTaskLists(taskInfo: TaskInfo, pageParams: PageParams,username:String): PageInfo<TaskInfo>
}
