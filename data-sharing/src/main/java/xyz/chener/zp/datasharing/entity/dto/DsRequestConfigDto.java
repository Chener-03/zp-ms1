package xyz.chener.zp.datasharing.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/04/06/16:56
 * @Email: chen@chener.xyz
 */
public class DsRequestConfigDto {


    private Integer id;

    private String requestName;

    private String requestUid;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orgId;

    private String orgName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    private String createUserName;

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

    // 今日调用次数
    private String dayCount;


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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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

    public String getDayCount() {
        return dayCount;
    }

    public void setDayCount(String dayCount) {
        this.dayCount = dayCount;
    }
}
