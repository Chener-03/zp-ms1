package xyz.chener.zp.zpusermodule.error.org;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class UserNotAuthError extends HttpErrorException {
    public UserNotAuthError() {
        super(R.HttpCode.HTTP_ERR.get(), "当前用户不能操作该组织");
    }
}
