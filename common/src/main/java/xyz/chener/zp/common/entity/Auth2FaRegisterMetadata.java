package xyz.chener.zp.common.entity;

import lombok.Data;

import java.io.Serializable;



@Data
public class Auth2FaRegisterMetadata implements Serializable {

    private String url;

    private Boolean require;

    public static class AuthResultCode {
        public static final int SUCCESS = 0;
        public static final int FAIL = 1;
        public static final int REQUIRE_AUTH = 2;
        public static final int NEED_AUTH = 3;
    }

}
