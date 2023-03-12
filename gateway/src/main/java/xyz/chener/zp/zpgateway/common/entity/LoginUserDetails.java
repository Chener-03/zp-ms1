package xyz.chener.zp.zpgateway.common.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: chenzp
 * @Date: 2023/01/11/13:47
 * @Email: chen@chener.xyz
 */
public class LoginUserDetails {

    private String token;

    private String username;

    private String password;

    private String ip;

    private String location;

    private String os;

    private Long expireTime;

    private Boolean isDisable;

    private String ds;

    private String system;

    public static class SystemEnum{
        public static final String WEB = "WEB";
        public static final String MOBILE = "ZP_ADMIN";
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    private Set<String> permissions= new HashSet<>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getDisable() {
        return isDisable;
    }

    public void setDisable(Boolean disable) {
        isDisable = disable;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
