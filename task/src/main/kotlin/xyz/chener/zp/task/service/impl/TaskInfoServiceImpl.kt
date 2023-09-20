package xyz.chener.zp.task.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import xyz.chener.zp.task.dao.TaskInfoDao
import xyz.chener.zp.task.entity.TaskInfo
import xyz.chener.zp.task.service.TaskInfoService


@Service
open class TaskInfoServiceImpl : ServiceImpl<TaskInfoDao?, TaskInfo?>(), TaskInfoService {




}

