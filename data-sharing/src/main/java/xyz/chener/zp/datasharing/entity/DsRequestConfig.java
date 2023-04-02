package xyz.chener.zp.datasharing.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * (DsRequestConfig)表实体类
 *
 * @author makejava
 * @since 2023-04-02 10:23:54
 */
@SuppressWarnings("serial")
public class DsRequestConfig extends Model<DsRequestConfig> {

    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String requestName;
    
    private String requestUid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orgId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    //日调用限制
    private Integer dayLimit;
    //单次返回字节限制
    private String byteReturnLimie;
    //单次请求参数字节限制
    private String byteReqLimit;
    //0 禁用  1启用
    private Boolean enable;
    //get  post ..... 
    private String requestMethod;
    //form    json   只有post时才区分
    private String paramType;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestUid() {
        return requestUid;
    }

    public void setRequestUid(String requestUid) {
        this.requestUid = requestUid;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(Integer dayLimit) {
        this.dayLimit = dayLimit;
    }

    public String getByteReturnLimie() {
        return byteReturnLimie;
    }

    public void setByteReturnLimie(String byteReturnLimie) {
        this.byteReturnLimie = byteReturnLimie;
    }

    public String getByteReqLimit() {
        return byteReqLimit;
    }

    public void setByteReqLimit(String byteReqLimit) {
        this.byteReqLimit = byteReqLimit;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }


    }

