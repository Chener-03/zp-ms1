package xyz.chener.zp.sentinelAdapter.circuitbreak.entity;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/13:00
 * @Email: chen@chener.xyz
 */
public class CircuitBreakRuleInfo {

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
