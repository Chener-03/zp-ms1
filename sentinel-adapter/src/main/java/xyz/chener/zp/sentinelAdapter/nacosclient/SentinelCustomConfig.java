package xyz.chener.zp.sentinelAdapter.nacosclient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.*;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/14:33
 * @Email: chen@chener.xyz
 */

@ConfigurationProperties(prefix = "zp.sentinel")
@RefreshScope
public class SentinelCustomConfig {

    private String serverAddr;

    private String username;

    private String password;


    private String groupId = "DEFAULT_GROUP";

    private String namespace;

    private Map<String,NacosDataSourceProperties> nacos = new HashMap<>();

    private Boolean enableAutoLoad = true;

    public Boolean getEnableAutoLoad() {
        return enableAutoLoad;
    }

    public void setEnableAutoLoad(Boolean enableAutoLoad) {
        this.enableAutoLoad = enableAutoLoad;
    }

    public Properties covertToProperties(){
        Properties properties = new Properties();
        properties.setProperty("serverAddr",serverAddr);
        properties.setProperty("username",username);
        properties.setProperty("password",password);
        properties.setProperty("groupId",groupId);
        properties.setProperty("namespace",namespace);
        return properties;
    }


    public Map<String, NacosDataSourceProperties> getNacos() {
        return nacos;
    }

    public void setNacos(Map<String, NacosDataSourceProperties> nacos) {
        this.nacos = nacos;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
