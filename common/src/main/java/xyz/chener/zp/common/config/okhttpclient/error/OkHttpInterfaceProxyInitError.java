package xyz.chener.zp.common.config.okhttpclient.error;

/**
 * @Author: chenzp
 * @Date: 2023/02/17/17:00
 * @Email: chen@chener.xyz
 */
public class OkHttpInterfaceProxyInitError extends RuntimeException{
    public OkHttpInterfaceProxyInitError(String message) {
        super("okhttp 创建接口代理异常 : "+message);
    }

    public OkHttpInterfaceProxyInitError() {
        super("okhttp 创建接口代理异常");
    }
}
