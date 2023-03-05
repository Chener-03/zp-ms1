package xyz.chener.zp.zpusermodule.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * (Notices)表实体类
 *
 * @author makejava
 * @since 2023-03-04 18:21:14
 */
@SuppressWarnings("serial")
public class Notices extends Model<Notices> {

    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Date createTime;
    
    private Date endTime;
    
    private String content;
    
    private String jmpLink;
    
    private String jmpLinkText;
    //类型: 1 发送所有用户    2发送部分用户  3 首页通知   4 仅在线用户通知
    private String type;

    private String ditch;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long publishUserId;

    private String users;

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getDitch() {
        return ditch;
    }

    public void setDitch(String ditch) {
        this.ditch = ditch;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getJmpLink() {
        return jmpLink;
    }

    public void setJmpLink(String jmpLink) {
        this.jmpLink = jmpLink;
    }

    public String getJmpLinkText() {
        return jmpLinkText;
    }

    public void setJmpLinkText(String jmpLinkText) {
        this.jmpLinkText = jmpLinkText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(Long publishUserId) {
        this.publishUserId = publishUserId;
    }


    }

