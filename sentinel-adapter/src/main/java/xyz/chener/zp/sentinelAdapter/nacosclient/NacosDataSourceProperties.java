package xyz.chener.zp.sentinelAdapter.nacosclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.language.bm.RuleType;
import org.springframework.core.env.Environment;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/14:30
 * @Email: chen@chener.xyz
 */
public class NacosDataSourceProperties {



    private String dataId;


    private String dataType = "json";


    private String ruleType;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }
}
