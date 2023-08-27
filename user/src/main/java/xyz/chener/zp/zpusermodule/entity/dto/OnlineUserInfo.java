package xyz.chener.zp.zpusermodule.entity.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class OnlineUserInfo implements Serializable {

    private String username;

    private String sessionId;

    private String ip;

    private String system;

}
