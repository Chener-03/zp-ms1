package xyz.chener.zp.common.config.okhttpclient.error;

import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/02/17/17:03
 * @Email: chen@chener.xyz
 */
public class OkHttpInterfaceRequestMethodError extends HttpErrorException {
    public OkHttpInterfaceRequestMethodError() {
        super("OkHttpInterfaceRequestMethodError:目前仅允许GET和POST");
    }

    public OkHttpInterfaceRequestMethodError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public OkHttpInterfaceRequestMethodError(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
