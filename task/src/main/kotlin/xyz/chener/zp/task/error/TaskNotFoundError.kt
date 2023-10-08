package xyz.chener.zp.task.error

import xyz.chener.zp.common.entity.R
import xyz.chener.zp.common.error.HttpErrorException

class TaskNotFoundError : HttpErrorException(R.HttpCode.HTTP_ERR.get(),"任务不存在")