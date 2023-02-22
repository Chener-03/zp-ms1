package xyz.chener.zp.zpusermodule.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class OrgInfoDto {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    //中文全称
    @NotNull(message = "机构名称不能为空")
    private String orgChName;
    //中文简称
    @NotNull(message = "机构简称不能为空")
    private String orgChSimpleName;
    //描述
    private String orgMessage;
    //类型
    private Integer orgType;
    //父Id
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "父机构不能为空")
    private Long parentId;
    //子机构数量
    @NotNull(message = "子机构数量不能为空")
    private Integer subsidiaryNum;
    //人数
    @NotNull(message = "人数不能为空")
    private Integer peopleNum;
    //等级
    private Integer orgLevel;
    //排序
    private Integer sortNum;
    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //是否禁用
    private Boolean disable;
    //负责人ID
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "负责人不能为空")
    private Long mainUserId;

    private String username;


    private String userChName;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "角色不能为空")
    private Long roleId;

    private String roleName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgChName() {
        return orgChName;
    }

    public void setOrgChName(String orgChName) {
        this.orgChName = orgChName;
    }

    public String getOrgChSimpleName() {
        return orgChSimpleName;
    }

    public void setOrgChSimpleName(String orgChSimpleName) {
        this.orgChSimpleName = orgChSimpleName;
    }

    public String getOrgMessage() {
        return orgMessage;
    }

    public void setOrgMessage(String orgMessage) {
        this.orgMessage = orgMessage;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSubsidiaryNum() {
        return subsidiaryNum;
    }

    public void setSubsidiaryNum(Integer subsidiaryNum) {
        this.subsidiaryNum = subsidiaryNum;
    }

    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    public Integer getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(Integer orgLevel) {
        this.orgLevel = orgLevel;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(Long mainUserId) {
        this.mainUserId = mainUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserChName() {
        return userChName;
    }

    public void setUserChName(String userChName) {
        this.userChName = userChName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
