package xyz.chener.zp.common.config.okhttpclient.error;

import xyz.chener.zp.common.error.HttpErrorException;

public class OkHttpResponseError extends HttpErrorException {


    private String bodyStr;


    public String getBodyStr() {
        return bodyStr;
    }

    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }


}
