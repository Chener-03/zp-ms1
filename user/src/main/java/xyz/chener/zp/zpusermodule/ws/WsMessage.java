package xyz.chener.zp.zpusermodule.ws;

import java.io.Serializable;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:35
 * @Email: chen@chener.xyz
 */
public class WsMessage implements Serializable {

    public static final WsMessage EMPTY_MESSAGE = new WsMessage(WsMessageConstVar.UNKNOWN_CODE);

    public WsMessage() {
    }

    public WsMessage(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
