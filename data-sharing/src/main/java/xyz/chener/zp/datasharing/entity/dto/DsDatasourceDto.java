package xyz.chener.zp.datasharing.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import xyz.chener.zp.common.config.paramDecryption.annotation.DecryField;
import xyz.chener.zp.common.config.query.annotation.QueryTableName;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField;
import xyz.chener.zp.datasharing.commons.PasswordEncry;

import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/04/03/11:56
 * @Email: chen@chener.xyz
 */


public class DsDatasourceDto {

    @QueryTableName("ds_datasource")
    private Integer id;
    //数据源名称

    @QueryTableName("ds_datasource")
    private String datasourceName;
    //链接host

    @QueryTableName("ds_datasource")
    private String host;


    @QueryTableName("ds_datasource")
    private String port;

    @QueryTableName("ds_datasource")
    private String databaseName;

    //链接参数
    @QueryTableName("ds_datasource")
    private String paramUrl;

    @EncryField(encryClass = PasswordEncry.class)
    @QueryTableName("ds_datasource")
    private String username;

    @EncryField(encryClass = PasswordEncry.class)
    @QueryTableName("ds_datasource")
    private String password;
    //组织ID
    @JsonSerialize(using = ToStringSerializer.class)
    @QueryTableName("ds_datasource")
    private Long orgId;

    private String orgSimpleName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @QueryTableName("ds_datasource")
    private Date createTime;

    @JsonSerialize(using = ToStringSerializer.class)
    @QueryTableName("ds_datasource")
    private Long createUserId;

    //mysql、oracle、
    @DecryField(required = false)
    @QueryTableName("ds_datasource")
    private String type;


    private Boolean healthy;

    public Boolean getHealthy() {
        return healthy;
    }

    public void setHealthy(Boolean healthy) {
        this.healthy = healthy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getParamUrl() {
        return paramUrl;
    }

    public void setParamUrl(String paramUrl) {
        this.paramUrl = paramUrl;
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

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgSimpleName() {
        return orgSimpleName;
    }

    public void setOrgSimpleName(String orgSimpleName) {
        this.orgSimpleName = orgSimpleName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
