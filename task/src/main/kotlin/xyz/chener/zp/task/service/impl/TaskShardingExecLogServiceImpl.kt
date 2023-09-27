package xyz.chener.zp.task.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import xyz.chener.zp.task.dao.TaskShardingExecLogDao
import xyz.chener.zp.task.entity.TaskShardingExecLog
import xyz.chener.zp.task.service.TaskShardingExecLogService


@Service
class TaskShardingExecLogServiceImpl : ServiceImpl<TaskShardingExecLogDao, TaskShardingExecLog>(),
    TaskShardingExecLogService
