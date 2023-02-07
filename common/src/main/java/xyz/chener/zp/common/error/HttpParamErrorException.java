package xyz.chener.zp.common.error;

import xyz.chener.zp.common.entity.R;

/**
 * @Author: chenzp
 * @Date: 2023/01/16/16:35
 * @Email: chen@chener.xyz
 */
public class HttpParamErrorException extends HttpErrorException{
    public HttpParamErrorException() {
        super(R.HttpCode.BAD_REQUEST.get(),R.ErrorMessage.BAD_REQUEST.get());
    }

    public HttpParamErrorException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public HttpParamErrorException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
