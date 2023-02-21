package xyz.chener.zp.zpusermodule.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/02/21/16:14
 * @Email: chen@chener.xyz
 */
public class OrgUserDto {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String userChName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date bindTime;

    private Boolean authDisable;

    private Boolean isOrgAdmin;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserChName() {
        return userChName;
    }

    public void setUserChName(String userChName) {
        this.userChName = userChName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    public Boolean getAuthDisable() {
        return authDisable;
    }

    public void setAuthDisable(Boolean authDisable) {
        this.authDisable = authDisable;
    }

    public Boolean getOrgAdmin() {
        return isOrgAdmin;
    }

    public void setOrgAdmin(Boolean orgAdmin) {
        isOrgAdmin = orgAdmin;
    }
}
