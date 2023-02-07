package xyz.chener.zp.zpusermodule.error.user;

import xyz.chener.zp.common.error.HttpErrorException;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;

/**
 * @Author: chenzp
 * @Date: 2023/01/16/11:29
 * @Email: chen@chener.xyz
 */
public class UserNotFoundException extends HttpErrorException {
    public UserNotFoundException() {
        super(LoginResult.ErrorResult.NO_USER);
    }

    public UserNotFoundException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public UserNotFoundException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
