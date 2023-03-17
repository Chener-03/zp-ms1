package xyz.chener.zp.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/17:14
 * @Email: chen@chener.xyz
 */


public class NacosServerInstance {

    private String name;
    private String groupName;
    private String clusters;
    private int cacheMillis;
    private List<Hosts> hosts;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastRefTime;
    private String checksum;
    private boolean allIPs;
    private boolean reachProtectionThreshold;
    private boolean valid;

    public static class Hosts {

        private String ip;
        private int port;
        private int weight;
        private boolean healthy;
        private boolean enabled;
        private boolean ephemeral;
        private String clusterName;
        private String serviceName;
        private Map<String,String> metadata;
        private int instanceHeartBeatTimeOut;
        private int ipDeleteTimeout;
        private int instanceHeartBeatInterval;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public boolean isHealthy() {
            return healthy;
        }

        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEphemeral() {
            return ephemeral;
        }

        public void setEphemeral(boolean ephemeral) {
            this.ephemeral = ephemeral;
        }

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }

        public int getInstanceHeartBeatTimeOut() {
            return instanceHeartBeatTimeOut;
        }

        public void setInstanceHeartBeatTimeOut(int instanceHeartBeatTimeOut) {
            this.instanceHeartBeatTimeOut = instanceHeartBeatTimeOut;
        }

        public int getIpDeleteTimeout() {
            return ipDeleteTimeout;
        }

        public void setIpDeleteTimeout(int ipDeleteTimeout) {
            this.ipDeleteTimeout = ipDeleteTimeout;
        }

        public int getInstanceHeartBeatInterval() {
            return instanceHeartBeatInterval;
        }

        public void setInstanceHeartBeatInterval(int instanceHeartBeatInterval) {
            this.instanceHeartBeatInterval = instanceHeartBeatInterval;
        }
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

    public String getClusters() {
        return clusters;
    }

    public void setClusters(String clusters) {
        this.clusters = clusters;
    }

    public int getCacheMillis() {
        return cacheMillis;
    }

    public void setCacheMillis(int cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    public List<Hosts> getHosts() {
        return hosts;
    }

    public void setHosts(List<Hosts> hosts) {
        this.hosts = hosts;
    }

    public Date getLastRefTime() {
        return lastRefTime;
    }

    public void setLastRefTime(Date lastRefTime) {
        this.lastRefTime = lastRefTime;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public boolean isAllIPs() {
        return allIPs;
    }

    public void setAllIPs(boolean allIPs) {
        this.allIPs = allIPs;
    }

    public boolean isReachProtectionThreshold() {
        return reachProtectionThreshold;
    }

    public void setReachProtectionThreshold(boolean reachProtectionThreshold) {
        this.reachProtectionThreshold = reachProtectionThreshold;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}