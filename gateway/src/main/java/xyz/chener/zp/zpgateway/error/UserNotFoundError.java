package xyz.chener.zp.zpgateway.error;

import xyz.chener.zp.zpgateway.common.error.HttpErrorException;

public class UserNotFoundError extends HttpErrorException {
    public UserNotFoundError() {
        super(401,"The jwt binding user was not found");
    }

    public UserNotFoundError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public UserNotFoundError(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
