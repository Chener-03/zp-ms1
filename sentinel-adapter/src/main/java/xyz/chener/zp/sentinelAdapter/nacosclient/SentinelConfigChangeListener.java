package xyz.chener.zp.sentinelAdapter.nacosclient;

import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.security.DeclareRoles;
import org.slf4j.Logger;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import xyz.chener.zp.sentinelAdapter.spho.SphoRuleManager;
import xyz.chener.zp.sentinelAdapter.sphu.SphuRuleManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/14:24
 * @Email: chen@chener.xyz
 */

@Configuration
@EnableConfigurationProperties(SentinelCustomConfig.class)
public class SentinelConfigChangeListener implements ApplicationListener<ApplicationStartedEvent> {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(SentinelConfigChangeListener.class);

    private final SentinelCustomConfig sentinelCustomConfig;

    public static final List<Consumer<List<FlowRule>>> flowChangeListeners = new CopyOnWriteArrayList<>();
    public static final List<Consumer<List<DegradeRule>>> degradeChangeListeners = new CopyOnWriteArrayList<>();

    private final ExecutorService pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(1), new NamedThreadFactory("sentinel-nacos-ds-update", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public SentinelConfigChangeListener(SentinelCustomConfig sentinelCustomConfig) {
        this.sentinelCustomConfig = sentinelCustomConfig;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (sentinelCustomConfig.getEnableAutoLoad()){
            try {
                Properties nacosProperties = sentinelCustomConfig.covertToProperties();
                ConfigService configService = NacosFactory.createConfigService(nacosProperties);
                sentinelCustomConfig.getNacos().values().forEach(e->{
                    try {
                        if (e.getRuleType().equals("flow")) {
                            String config = configService.getConfig(e.getDataId(), sentinelCustomConfig.getGroupId(), 5000);
                            processFlowJson(config);
                            configService.addListener(e.getDataId(), sentinelCustomConfig.getGroupId(), new Listener() {
                                        @Override
                                        public Executor getExecutor() {
                                            return pool;
                                        }

                                        @Override
                                        public void receiveConfigInfo(String configInfo) {
                                            processFlowJson(configInfo);
                                        }
                                    });
                        }
                        if (e.getRuleType().equals("degrade")) {
                            String config = configService.getConfig(e.getDataId(), sentinelCustomConfig.getGroupId(), 5000);
                            processDegradeJson(config);
                            configService.addListener(e.getDataId(), sentinelCustomConfig.getGroupId(), new Listener() {
                                        @Override
                                        public Executor getExecutor() {
                                            return pool;
                                        }

                                        @Override
                                        public void receiveConfigInfo(String configInfo) {
                                            processDegradeJson(configInfo);
                                        }
                                    });
                        }
                    }catch (Exception exi){
                        throw new RuntimeException(exi);
                    }
                });
            }catch (Exception exception){
                logger.error("sentinel nacos config listener init error",exception);
            }
        }

    }

    private void processFlowJson(String configInfo) {
        try {
            List<Map> list = new ObjectMapper().readValue(configInfo, List.class);
            ArrayList<FlowRule> flowRules = new ArrayList<>();
            for (Map map : list) {
                FlowRule flowRule = parseFlowRule(map);
                if (flowRule != null) {
                    flowRules.add(flowRule);
                }
            }
            flowRules.forEach(fl->{
                SphoRuleManager.removeRules(fl.getResource());
                SphoRuleManager.addRules(fl.getResource(),fl);
            });
            flowChangeListeners.forEach(consumer -> consumer.accept(flowRules));
        } catch (Exception ex) {}
    }

    private void processDegradeJson(String json){
        try {
            List<Map> list = new ObjectMapper().readValue(json, List.class);
            ArrayList<DegradeRule> degradeRules = new ArrayList<>();
            for (Map map : list) {
                DegradeRule degradeRule = parseDegradeRule(map);
                if (degradeRule != null) {
                    degradeRules.add(degradeRule);
                }
            }
            degradeRules.forEach(fl->{
                SphuRuleManager.removeRules(fl.getResource());
                SphuRuleManager.addRules(fl.getResource(),fl);
            });
            degradeChangeListeners.forEach(consumer -> consumer.accept(degradeRules));
        } catch (Exception ex) {}
    }

    private FlowRule parseFlowRule(Map map){
        try {
            FlowRule res = new FlowRule();
            res.setResource(map.get("resource")==null?null:map.get("resource").toString());
            if (map.get("grade")!=null)
                res.setGrade(Integer.parseInt(map.get("grade").toString()));
            if (map.get("count")!=null)
                res.setCount(Double.parseDouble(map.get("count").toString()));
            res.setLimitApp(map.get("limitApp")==null?null:map.get("limitApp").toString());
            if (map.get("strategy")!=null)
                res.setStrategy(Integer.parseInt(map.get("strategy").toString()));
            if (map.get("controlBehavior")!=null)
                res.setControlBehavior(Integer.parseInt(map.get("controlBehavior").toString()));
            if (map.get("clusterMode")!=null)
                res.setClusterMode(Boolean.parseBoolean(map.get("clusterMode").toString()));
            if (map.get("warmUpPeriodSec")!=null)
                res.setWarmUpPeriodSec(Integer.parseInt(map.get("warmUpPeriodSec").toString()));
            if (map.get("maxQueueingTimeMs")!=null)
                res.setMaxQueueingTimeMs(Integer.parseInt(map.get("maxQueueingTimeMs").toString()));
            return res;
        }catch (Exception ignored){}
        return null;
    }

    private DegradeRule parseDegradeRule(Map map){
        try {
            DegradeRule res = new DegradeRule();
            res.setResource(map.get("resource")==null?null:map.get("resource").toString());
            if (map.get("grade")!=null)
                res.setGrade(Integer.parseInt(map.get("grade").toString()));
            if (map.get("count")!=null)
                res.setCount(Double.parseDouble(map.get("count").toString()));
            res.setLimitApp(map.get("limitApp")==null?null:map.get("limitApp").toString());
            if (map.get("timeWindow")!=null)
                res.setTimeWindow(Integer.parseInt(map.get("timeWindow").toString()));
            if (map.get("minRequestAmount")!=null)
                res.setMinRequestAmount(Integer.parseInt(map.get("minRequestAmount").toString()));
            if (map.get("statIntervalMs")!=null)
                res.setStatIntervalMs(Integer.parseInt(map.get("statIntervalMs").toString()));
            if (map.get("slowRatioThreshold")!=null)
                res.setSlowRatioThreshold(Double.parseDouble(map.get("slowRatioThreshold").toString()));

            return res;
        }catch (Exception ignored){}
        return null;
    }

}
