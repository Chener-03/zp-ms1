package xyz.chener.zp.task.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import xyz.chener.zp.task.dao.TaskLogDao
import xyz.chener.zp.task.entity.TaskLog
import xyz.chener.zp.task.service.TaskLogService


@Service
class TaskLogServiceImpl : ServiceImpl<TaskLogDao, TaskLog>(),TaskLogService
