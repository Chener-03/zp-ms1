package xyz.chener.zp.common.config.okhttpclient.error;

import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/02/17/17:06
 * @Email: chen@chener.xyz
 */
public class OkHttpInterfaceUrlBuildError extends HttpErrorException {
    public OkHttpInterfaceUrlBuildError() {
        super("OkHttpInterfaceUrlBuildError: URL构建异常");
    }

    public OkHttpInterfaceUrlBuildError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public OkHttpInterfaceUrlBuildError(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
