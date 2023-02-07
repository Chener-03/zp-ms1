package xyz.chener.zp.zpusermodule.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/01/16/11:06
 * @Email: chen@chener.xyz
 */
public class LoginResult {

    private boolean success;

    private String token;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;
    private String lastLoginIp;

    public static class  ErrorResult{
        public static final String NO_USER = "不存在的用户";
        public static final String USER_DISABLE = "用户被禁止";
        public static final String USER_EXPIRE = "用户过期";
        public static final String USERNAME_PASSWORD_ERROR = "用户名或密码错误";
        public static final String SERVER_ERROR = "服务器错误";
        public static final String GOOGLE_FALSE = "人机校验失败";
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
