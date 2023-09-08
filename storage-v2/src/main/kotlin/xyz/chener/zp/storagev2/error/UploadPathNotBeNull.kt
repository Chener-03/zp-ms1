package xyz.chener.zp.storagev2.error

import xyz.chener.zp.common.entity.R
import xyz.chener.zp.common.error.HttpErrorException

class UploadPathNotBeNull : HttpErrorException(R.HttpCode.HTTP_ERR.get(),"上传路径不能为空") {
}