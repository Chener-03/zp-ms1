package xyz.chener.zp.zpusermodule.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * (OrgBase)表实体类
 *
 * @author makejava
 * @since 2023-02-16 10:45:33
 */
@SuppressWarnings("serial")
public class OrgBase extends Model<OrgBase> {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    //中文全称
    private String orgChName;
    //中文简称
    private String orgChSimpleName;
    //描述
    private String orgMessage;
    //类型
    private Integer orgType;
    //父Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    //子机构数量
    private Integer subsidiaryNum;
    //人数
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
    private Long mainUserId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

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



    public Long getMainUserId() {
        return mainUserId;
    }

    public void setMainUserId(Long mainUserId) {
        this.mainUserId = mainUserId;
    }


    }

