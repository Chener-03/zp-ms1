package xyz.chener.zp.zpgateway.error;

import xyz.chener.zp.zpgateway.common.error.HttpErrorException;

public class SystemCheckError extends HttpErrorException {
    public SystemCheckError() {
        super(401,"授权系统校验失败");
    }

    public SystemCheckError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public SystemCheckError(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}