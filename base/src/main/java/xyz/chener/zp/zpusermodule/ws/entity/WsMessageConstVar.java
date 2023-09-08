package xyz.chener.zp.zpusermodule.ws.entity;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:40
 * @Email: chen@chener.xyz
 */
// 页面消息传输实体类code枚举

public class WsMessageConstVar {

    // 未知
    public static final int UNKNOWN_CODE = 0;

    // 心跳
    public static final int HEART_BEAT_CODE = 1;

    // 推送消息通知
    public static final int MESSAGE_NOTIFY = 2;



    // 二维码登录相关
    public static final int QRCODE_LOGIN_REQUEST = 3;
    public static final int QRCODE_LOGIN_RESPONSE = 4;
    public static final int QRCODE_LOGIN_FAIL = 5;
    public static final int QRCODE_LOGIN_DOLOGIN = 6;
    public static final int QRCODE_LOGIN_READY = 7;



}
