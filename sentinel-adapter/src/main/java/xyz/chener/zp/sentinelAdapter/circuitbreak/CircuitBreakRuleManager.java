package xyz.chener.zp.sentinelAdapter.circuitbreak;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import xyz.chener.zp.sentinelAdapter.circuitbreak.entity.CircuitBreakRuleInfo;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/12:42
 * @Email: chen@chener.xyz
 */
public class CircuitBreakRuleManager {

    //  key:resource+uuid
    public static final ConcurrentHashMap<String, CircuitBreakRuleInfo> cache = new ConcurrentHashMap<>();
    //  key:resource
    public static final ConcurrentHashMap<String, DegradeRule> rules = new ConcurrentHashMap<>();


    public static void addRules(String resource,DegradeRule rule){
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
            DegradeRule degradeRule = rules.get(resource);
            degradeRule.setResource(resource + uuid);
            loadRule(degradeRule);
            CircuitBreakRuleInfo circuitBreakRuleInfo = new CircuitBreakRuleInfo();
            circuitBreakRuleInfo.setKey(resource + uuid);
            circuitBreakRuleInfo.setResource(resource);
            cache.put(resource + uuid, circuitBreakRuleInfo);
            return resource + uuid;
        }
        return null;
    }


    private static synchronized void loadRule(DegradeRule degradeRule){
        ArrayList<DegradeRule> degradeRules = new ArrayList<>(DegradeRuleManager.getRules());
        degradeRules.add(degradeRule);
        DegradeRuleManager.loadRules(degradeRules);
    }

    private static synchronized void removeRule(String resource){
        ArrayList<DegradeRule> degradeRules = new ArrayList<>(DegradeRuleManager.getRules());
        degradeRules.removeIf(degradeRule -> degradeRule.getResource().equals(resource));
        DegradeRuleManager.loadRules(degradeRules);
    }

}
