package xyz.chener.zp.zpgateway.error;

import xyz.chener.zp.zpgateway.common.error.HttpErrorException;

public class UserAuthNotFoundError extends HttpErrorException {
    public UserAuthNotFoundError() {
        super(401,"user role not found!");
    }

    public UserAuthNotFoundError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public UserAuthNotFoundError(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
