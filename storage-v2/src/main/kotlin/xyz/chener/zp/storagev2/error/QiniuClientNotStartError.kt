package xyz.chener.zp.storagev2.error

import xyz.chener.zp.common.entity.R
import xyz.chener.zp.common.error.HttpErrorException

class QiniuClientNotStartError : HttpErrorException(R.HttpCode.HTTP_ERR.get(),"七牛云客户端未启动")