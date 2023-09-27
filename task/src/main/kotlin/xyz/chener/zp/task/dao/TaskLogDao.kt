package xyz.chener.zp.task.dao

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import xyz.chener.zp.task.entity.TaskLog


@Mapper
interface TaskLogDao : BaseMapper<TaskLog?>

