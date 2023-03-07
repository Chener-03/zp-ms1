package xyz.chener.zp.zpusermodule.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/03/07/16:26
 * @Email: chen@chener.xyz
 */
public class MessagesDto {

    private Integer id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String username;

    private String title;
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

    private String sendUserName;

    private String jmpLink;

    private String jmpLinkText;

    @TableLogic(value = "0",delval = "1")
    private Integer isdelete;


    private Boolean senderDelete;
    private Boolean receiveDelete;

    private Integer refMessageId;

    private String refMessageTitle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refMessageDate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
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

    public Integer getRefMessageId() {
        return refMessageId;
    }

    public void setRefMessageId(Integer refMessageId) {
        this.refMessageId = refMessageId;
    }

    public String getRefMessageTitle() {
        return refMessageTitle;
    }

    public void setRefMessageTitle(String refMessageTitle) {
        this.refMessageTitle = refMessageTitle;
    }

    public Date getRefMessageDate() {
        return refMessageDate;
    }

    public void setRefMessageDate(Date refMessageDate) {
        this.refMessageDate = refMessageDate;
    }
}
