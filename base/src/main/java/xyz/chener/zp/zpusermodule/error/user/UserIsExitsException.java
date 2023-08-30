package xyz.chener.zp.zpusermodule.error.user;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class UserIsExitsException extends HttpErrorException {
    public UserIsExitsException() {
        super(R.HttpCode.HTTP_ERR.get(),"用户已存在");
    }

    public UserIsExitsException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public UserIsExitsException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
