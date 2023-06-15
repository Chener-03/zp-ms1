package xyz.chener.zp.zpusermodule.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: chenzp
 * @Date: 2023/06/15/15:17
 * @Email: chen@chener.xyz
 */

@Data
public class QrCodeLoginCache implements Serializable {
    private String uuid;
    private String sessionId;
    private String host;
    private Integer port;
    private String ipAddr;
    private String os;
}
