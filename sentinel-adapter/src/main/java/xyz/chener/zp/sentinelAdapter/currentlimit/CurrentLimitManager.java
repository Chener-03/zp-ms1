package xyz.chener.zp.sentinelAdapter.currentlimit;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import xyz.chener.zp.sentinelAdapter.circuitbreak.entity.CircuitBreakRuleInfo;
import xyz.chener.zp.sentinelAdapter.currentlimit.entity.CurrentLimitRuleInfo;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/14:18
 * @Email: chen@chener.xyz
 */
public class CurrentLimitManager {
    //  key:resource+uuid
    public static final ConcurrentHashMap<String, CurrentLimitRuleInfo> cache = new ConcurrentHashMap<>();
    //  key:resource
    public static final ConcurrentHashMap<String, FlowRule> rules = new ConcurrentHashMap<>();


    public static void addRules(String resource,FlowRule rule){
        rules.put(resource,rule);
    }

    public static void removeRules(String resource){
        rules.remove(resource);
        ArrayList<String> removeKeys = new ArrayList<>();
        cache.forEach((key, value) -> {
            if (value.getResource().equals(resource)) {
                removeKeys.add(key);
            }
        });
        removeKeys.forEach(key -> {
            removeRule(key);
            cache.remove(key);
        });
    }

    public static void verifyAllRules(){
        ArrayList<String> removeKeys = new ArrayList<>();
        cache.forEach((key, value) -> {
            if (!rules.containsKey(value.getResource())) {
                removeKeys.add(key);
            }
        });
        removeKeys.forEach(key -> {
            removeRule(key);
            cache.remove(key);
        });
    }

    public static String getRulesName(String resource,String uuid){
        Objects.requireNonNull(resource);
        Objects.requireNonNull(uuid);
        if (cache.containsKey(resource+uuid)) {
            return cache.get(resource+uuid).getKey();
        }
        if (rules.containsKey(resource)) {
            FlowRule flowRule = rules.get(resource);
            flowRule.setResource(resource + uuid);
            loadRule(flowRule);
            CurrentLimitRuleInfo currentLimitRuleInfo = new CurrentLimitRuleInfo();
            currentLimitRuleInfo.setKey(resource + uuid);
            currentLimitRuleInfo.setResource(resource);
            cache.put(resource + uuid, currentLimitRuleInfo);
            return resource + uuid;
        }
        return null;
    }


    private static synchronized void loadRule(FlowRule flowRule){
        ArrayList<FlowRule> flowRules = new ArrayList<>(FlowRuleManager.getRules());
        flowRules.add(flowRule);
        FlowRuleManager.loadRules(flowRules);
    }

    private static synchronized void removeRule(String resource){
        ArrayList<FlowRule> flowRules = new ArrayList<>(FlowRuleManager.getRules());
        flowRules.removeIf(flowRule -> flowRule.getResource().equals(resource));
        FlowRuleManager.loadRules(flowRules);
    }

}
