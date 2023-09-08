package xyz.chener.zp.zpgateway.common.entity;

import xyz.chener.zp.zpgateway.entity.SecurityVar;

public class CommonVar {

    public static final String IP_HEAD = "real-ip-address";

    // 存放在header中的用户名
    public static final String REQUEST_USER = "request-user-0";

    // 存放在header中的用户权限
    public static final String REQUEST_USER_AUTH = "request-user-auth-list";

    // 存放在header中的用户对象
    public static final String REQUEST_USER_OBJECT = "request-user-0-object";

    public static final String REQUEST_JWT_ENTITY_JSON_BASE64 = "request-jwt-entity-json-base64";

    public static final String SERVICE_CALL_AUTH_NAME = SecurityVar.ROLE_PREFIX+"microservice_call";
    public static final String OPEN_FEIGN_HEADER = "open-feign-custom-header";
    public static final String FA_HEADER_KEY = "A_2FA_AUTH";

    public static final String HUMAN_VERIFY_HEADER_KEY = "A_HUMAN_VERIFY";

    public static final String WEB_URL_PREFIX = "/api/web";
    public static final String CLIENT_URL_PREFIX = "/api/client";

}
