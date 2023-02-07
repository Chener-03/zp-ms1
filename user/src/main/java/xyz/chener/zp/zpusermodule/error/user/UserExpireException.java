package xyz.chener.zp.zpusermodule.error.user;

import xyz.chener.zp.common.error.HttpErrorException;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;


/**
 * @Author: chenzp
 * @Date: 2023/01/16/11:48
 * @Email: chen@chener.xyz
 */
public class UserExpireException extends HttpErrorException {
    public UserExpireException() {
        super(LoginResult.ErrorResult.USER_EXPIRE);
    }

    public UserExpireException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public UserExpireException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
