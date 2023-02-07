package xyz.chener.zp.zpgateway.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "gateway")
@RefreshScope
public class GatewayCostomProperties {

    private final Ip ip = new Ip();

    public Ip getIp() {
        return ip;
    }

    public static class Ip{
        private Boolean enable = false;

        private Boolean isWhite = false;

        private List<String> ipList = new ArrayList<>();

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public Boolean getIsWhite() {
            return isWhite;
        }

        public void setIsWhite(Boolean white) {
            isWhite = white;
        }

        public List<String> getIpList() {
            return ipList;
        }

        public void setIpList(List<String> ipList) {
            this.ipList = ipList;
        }
    }

}
