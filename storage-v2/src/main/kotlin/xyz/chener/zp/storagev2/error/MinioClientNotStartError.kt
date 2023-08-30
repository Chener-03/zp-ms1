package xyz.chener.zp.storagev2.error

import xyz.chener.zp.common.entity.R
import xyz.chener.zp.common.error.HttpErrorException

class MinioClientNotStartError  : HttpErrorException(R.HttpCode.HTTP_ERR.get(),"Minio客户端未启动")