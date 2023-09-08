package xyz.chener.zp.zpusermodule.error.user;

import xyz.chener.zp.common.error.HttpErrorException;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;

/**
 * @Author: chenzp
 * @Date: 2023/01/16/11:48
 * @Email: chen@chener.xyz
 */
public class UserDisableException extends HttpErrorException {
    public UserDisableException() {
        super(LoginResult.ErrorResult.USER_DISABLE);
    }

    public UserDisableException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public UserDisableException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
