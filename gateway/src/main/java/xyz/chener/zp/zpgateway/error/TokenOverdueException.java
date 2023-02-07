package xyz.chener.zp.zpgateway.error;

import xyz.chener.zp.zpgateway.common.error.HttpErrorException;

public class TokenOverdueException extends HttpErrorException {
    public TokenOverdueException() {
        super(401,"Token expired");
    }

    public TokenOverdueException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public TokenOverdueException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
