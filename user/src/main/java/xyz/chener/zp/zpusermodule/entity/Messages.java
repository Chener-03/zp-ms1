package xyz.chener.zp.zpusermodule.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * (Messages)表实体类
 *
 * @author makejava
 * @since 2023-03-05 12:31:57
 */
@SuppressWarnings("serial")
public class Messages extends Model<Messages> {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    
    private String content;
    //文本类型: text or other
    private String type;
    //普通0  重要1  非常重要2
    private String imp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;
    //false:0  
    private Boolean isread;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sendUserId;
    
    private String jmpLink;
    
    private String jmpLinkText;

    @TableLogic(value = "0",delval = "1")
    private Integer isdelete;

    @TableField()
    private Boolean senderDelete;
    private Boolean receiveDelete;


    public static class Type{
        public static final String TEXT = "text";
        public static final String OTHER = "other";
    }

    public static class Imp{
        public static final String NORMAL = "0";
        public static final String IMPORTANT = "1";
        public static final String VERY_IMPORTANT = "2";
    }

    public Boolean getSenderDelete() {
        return senderDelete;
    }

    public void setSenderDelete(Boolean senderDelete) {
        this.senderDelete = senderDelete;
    }

    public Boolean getReceiveDelete() {
        return receiveDelete;
    }

    public void setReceiveDelete(Boolean receiveDelete) {
        this.receiveDelete = receiveDelete;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImp() {
        return imp;
    }

    public void setImp(String imp) {
        this.imp = imp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Boolean getIsread() {
        return isread;
    }

    public void setIsread(Boolean isread) {
        this.isread = isread;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
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

    public Integer getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }


    }

