package xyz.chener.zp.zpgateway.error;

import xyz.chener.zp.zpgateway.common.error.HttpErrorException;

public class UserNotFoundError extends HttpErrorException {
    public UserNotFoundError() {
        super(401,"登录用户未找到");
    }

    public UserNotFoundError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public UserNotFoundError(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
