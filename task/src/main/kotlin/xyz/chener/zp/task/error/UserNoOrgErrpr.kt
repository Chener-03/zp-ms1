package xyz.chener.zp.task.error

import xyz.chener.zp.common.entity.R
import xyz.chener.zp.common.error.HttpErrorException

class UserNoOrgErrpr: HttpErrorException(R.HttpCode.HTTP_ERR.get(),"用户没有组织")
