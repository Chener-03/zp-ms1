package xyz.chener.zp.task.error

import xyz.chener.zp.common.entity.R
import xyz.chener.zp.common.error.HttpErrorException

class LoadTaskError : HttpErrorException {

    constructor() : super(R.HttpCode.HTTP_ERR.get(), "任务加载失败")

    constructor(message: String?) : super(R.HttpCode.HTTP_ERR.get(), "任务加载失败:$message")

}