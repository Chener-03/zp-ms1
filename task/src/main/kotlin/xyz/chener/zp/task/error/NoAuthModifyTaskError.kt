package xyz.chener.zp.task.error

import xyz.chener.zp.common.entity.R
import xyz.chener.zp.common.error.HttpErrorException

class NoAuthModifyTaskError : HttpErrorException(R.HttpCode.HTTP_ERR.get(),"无权限修改任务") {
}