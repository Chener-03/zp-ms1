package xyz.chener.zp.zpusermodule.ws.entity;

import lombok.Builder;

import java.io.Serializable;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:35
 * @Email: chen@chener.xyz
 * @Description: 页面消息传输实体类
 */

@Builder
public class WsMessage implements Serializable {

    public static final WsMessage EMPTY_MESSAGE = new WsMessage(WsMessageConstVar.UNKNOWN_CODE);

    public WsMessage() {
    }

    public WsMessage(Integer code) {
        this.code = code;
    }

    public WsMessage(Integer code, String jwt, String username, String message, Object obj) {
        this.code = code;
        this.jwt = jwt;
        this.username = username;
        this.message = message;
        this.obj = obj;
    }

    private Integer code;

    private String jwt;

    private String username;

    private String message;

    private Object obj;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
