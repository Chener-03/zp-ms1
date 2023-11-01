package xyz.chener.zp.zpgateway.common.entity;

import lombok.Data;

/**
 * @Author: chenzp
 * @Date: 2023/01/11/13:47
 * @Email: chen@chener.xyz
 */
@Data
public class LoginUserDetails {

    private Long userId;

    private String username;

    private String ip;

    private String os;

    private String ds;

    private String system;

    public static class SystemEnum{
        public static final String WEB = "WEB";
        public static final String CLIENT = "CLIENT";
    }

}