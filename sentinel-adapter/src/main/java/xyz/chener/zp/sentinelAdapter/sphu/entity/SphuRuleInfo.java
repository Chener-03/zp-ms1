package xyz.chener.zp.sentinelAdapter.sphu.entity;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/13:00
 * @Email: chen@chener.xyz
 */
public class SphuRuleInfo {

    // resource+uuid
    private String key;

    // resource
    private String resource;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
