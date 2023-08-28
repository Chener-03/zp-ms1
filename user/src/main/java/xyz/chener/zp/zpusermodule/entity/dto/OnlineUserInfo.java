package xyz.chener.zp.zpusermodule.entity.dto;


import lombok.Data;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;

import java.io.Serializable;

@Data
public class OnlineUserInfo implements Serializable {

    private String username;

    private String sessionId;

    private String ip;

    private String system;

    @EncryField
    private String realSessionId;

}
