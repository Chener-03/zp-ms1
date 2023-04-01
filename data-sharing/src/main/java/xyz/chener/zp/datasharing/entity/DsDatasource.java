package xyz.chener.zp.datasharing.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import xyz.chener.zp.common.config.paramDecryption.annotation.DecryField;

import java.io.Serializable;

/**
 * (DsDatasource)表实体类
 *
 * @author makejava
 * @since 2023-03-31 22:51:39
 */
@SuppressWarnings("serial")
public class DsDatasource extends Model<DsDatasource> {

    @TableId(type = IdType.AUTO)
    @DecryField(required = false)
    private Integer id;
    //数据源名称
    @DecryField(required = false)
    @NotNull(message = "数据源名称不能为空")
    private String datasourceName;
    //链接host
    @NotNull(message = "host不能为空")
    @DecryField(required = false)
    private String host;

    @NotNull(message = "port不能为空")
    @DecryField(required = false)
    private String port;

    @DecryField(required = false)
    private String databaseName;
    //链接参数
    @DecryField(required = false)
    private String paramUrl;


    private String username;
    
    private String password;
    //组织ID
    @JsonSerialize(using = ToStringSerializer.class)
    @DecryField(required = false)
    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DecryField(required = false)
    private Date createTime;

    @JsonSerialize(using = ToStringSerializer.class)
    @DecryField(required = false)
    private Long createUserId;

    //mysql、oracle、
    @DecryField(required = false)
    @NotNull(message = "数据源类型不能为空")
    private String type;


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

