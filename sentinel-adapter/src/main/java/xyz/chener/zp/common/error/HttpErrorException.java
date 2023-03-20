package xyz.chener.zp.common.error;

import xyz.chener.zp.common.entity.R;


public class HttpErrorException extends RuntimeException {
    private int httpCode;

    private String httpErrorMessage;

    public HttpErrorException()
    {
        httpCode = R.HttpCode.HTTP_ERR.get();
        httpErrorMessage = super.getMessage();
    }

    public HttpErrorException(int httpCode, String httpErrorMessage) {
        super(httpErrorMessage);
        this.httpCode = httpCode;
        this.httpErrorMessage = httpErrorMessage;
    }

    public HttpErrorException(String httpErrorMessage) {
        super(httpErrorMessage);
        httpCode = R.HttpCode.HTTP_ERR.get();
        this.httpErrorMessage = httpErrorMessage;
    }

    @Override
    public String getMessage() {
        return this.httpErrorMessage;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getHttpErrorMessage() {
        return httpErrorMessage;
    }

    public void setHttpErrorMessage(String httpErrorMessage) {
        this.httpErrorMessage = httpErrorMessage;
    }
}
