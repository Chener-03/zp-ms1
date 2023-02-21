package xyz.chener.zp.zpusermodule.entity.dto;

/**
 * @Author: chenzp
 * @Date: 2023/02/21/15:29
 * @Email: chen@chener.xyz
 */
public class OrgExtendInfoDto {

    private Integer subOrgCount;

    private Integer userCount;

    private Integer activeUserCount;

    public Integer getSubOrgCount() {
        return subOrgCount;
    }

    public void setSubOrgCount(Integer subOrgCount) {
        this.subOrgCount = subOrgCount;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getActiveUserCount() {
        return activeUserCount;
    }

    public void setActiveUserCount(Integer activeUserCount) {
        this.activeUserCount = activeUserCount;
    }
}
