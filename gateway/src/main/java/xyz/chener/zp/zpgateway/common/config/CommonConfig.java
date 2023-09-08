package xyz.chener.zp.zpgateway.common.config;

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

@Component
@ConfigurationProperties(prefix = "zp")
@RefreshScope
public class CommonConfig {

    private final Jwt jwt = new Jwt();

    private final Security security = new Security();

    private final LoggerPush loggerPush = new LoggerPush();
    private final MybatisCache mybatisCache = new MybatisCache();

    public MybatisCache getMybatisCache() {
        return mybatisCache;
    }

    public LoggerPush getLoggerPush() {
        return loggerPush;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Security getSecurity() {
        return security;
    }



    public static class Jwt{
        private String salt = "qwer";
        private Long expires = 1000*60*60L;
        private Long clientExpires = 1000 * 60 * 60 * 24 *30L;

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public Long getExpires() {
            return expires;
        }

        public void setExpires(Long expires) {
            this.expires = expires;
        }

        public Long getClientExpires() {
            return clientExpires;
        }

        public void setClientExpires(Long clientExpires) {
            this.clientExpires = clientExpires;
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

    public static class LoggerPush{
        private String esHost = "";

        private String exPort = "";

        private String esUsername = "";

        private String esPassword = "";

        private String esIndexName = "zplogger";

        private Integer retryCount = 3;

        private String failPath = "./logs/logger-push-fail.log";

        public Integer getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(Integer retryCount) {
            this.retryCount = retryCount;
        }

        public String getFailPath() {
            return failPath;
        }

        public void setFailPath(String failPath) {
            this.failPath = failPath;
        }

        public String getEsIndexName() {
            return esIndexName;
        }

        public void setEsIndexName(String esIndexName) {
            this.esIndexName = esIndexName;
        }

        public String getEsHost() {
            return esHost;
        }

        public void setEsHost(String esHost) {
            this.esHost = esHost;
        }

        public String getExPort() {
            return exPort;
        }

        public void setExPort(String exPort) {
            this.exPort = exPort;
        }

        public String getEsUsername() {
            return esUsername;
        }

        public void setEsUsername(String esUsername) {
            this.esUsername = esUsername;
        }

        public String getEsPassword() {
            return esPassword;
        }

        public void setEsPassword(String esPassword) {
            this.esPassword = esPassword;
        }
    }

    public static class MybatisCache{
        private String redisCachePrefix = "mybatis-cache:";
        private Integer redisCacheExpireMs = 1000*60*60;

        private Boolean enableDealyDelete = true;

        public Boolean getEnableDealyDelete() {
            return enableDealyDelete;
        }

        public void setEnableDealyDelete(Boolean enableDealyDelete) {
            this.enableDealyDelete = enableDealyDelete;
        }

        public String getRedisCachePrefix() {
            return redisCachePrefix;
        }

        public void setRedisCachePrefix(String redisCachePrefix) {
            this.redisCachePrefix = redisCachePrefix;
        }

        public Integer getRedisCacheExpireMs() {
            return redisCacheExpireMs;
        }

        public void setRedisCacheExpireMs(Integer redisCacheExpireMs) {
            this.redisCacheExpireMs = redisCacheExpireMs;
        }
    }

}
