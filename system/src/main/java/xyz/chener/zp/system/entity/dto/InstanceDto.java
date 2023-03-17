package xyz.chener.zp.system.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import xyz.chener.zp.system.entity.NacosServerInstance;

import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/03/17/09:35
 * @Email: chen@chener.xyz
 */
public class InstanceDto {

    private NacosServerInstance.Hosts hosts;

    private String name;
    private String groupName;

    private String namespace;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastRefTime;

    public NacosServerInstance.Hosts getHosts() {
        return hosts;
    }

    public void setHosts(NacosServerInstance.Hosts hosts) {
        this.hosts = hosts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Date getLastRefTime() {
        return lastRefTime;
    }

    public void setLastRefTime(Date lastRefTime) {
        this.lastRefTime = lastRefTime;
    }
}
