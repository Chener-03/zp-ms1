package xyz.chener.zp.zpusermodule.error.user;

import xyz.chener.zp.common.error.HttpErrorException;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;

/**
 * @Author: chenzp
 * @Date: 2023/01/16/11:52
 * @Email: chen@chener.xyz
 */
public class UsernamePasswordErrorException extends HttpErrorException {
    public UsernamePasswordErrorException() {
        super(LoginResult.ErrorResult.USERNAME_PASSWORD_ERROR);
    }

    public UsernamePasswordErrorException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public UsernamePasswordErrorException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
