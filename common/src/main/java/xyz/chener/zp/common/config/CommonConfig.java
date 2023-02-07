package xyz.chener.zp.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/01/13/08:12
 * @Email: chen@chener.xyz
 */


@ConfigurationProperties(prefix = "zp")
@RefreshScope
public class CommonConfig {

    private final Jwt jwt = new Jwt();

    private final Security security = new Security();

    public Jwt getJwt() {
        return jwt;
    }

    public Security getSecurity() {
        return security;
    }

    public static class Jwt{
        private String salt = "qwer";
        private Integer expires = 1000*60*60;

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public Integer getExpires() {
            return expires;
        }

        public void setExpires(Integer expires) {
            this.expires = expires;
        }
    }

    public static class Security{
        private List<String> writeList = new ArrayList<>();

        private String feignCallSlat = "9OXoyBoyOMzafOCcNBEJDuRlu80IGyrGFJPef";

        private String dsKey = "67F9695B82E38130C305BC67";

        public String getDsKey() {
            return dsKey;
        }

        public void setDsKey(String dsKey) {
            this.dsKey = dsKey;
        }

        public String getFeignCallSlat() {
            return feignCallSlat;
        }

        public void setFeignCallSlat(String feignCallSlat) {
            this.feignCallSlat = feignCallSlat;
        }

        public List<String> getWriteList() {
            return writeList;
        }

        public void setWriteList(List<String> writeList) {
            this.writeList = writeList;
        }
    }

}
