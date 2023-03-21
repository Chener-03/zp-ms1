package xyz.chener.zp.system.service.impl;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.okhttpclient.HttpRequestContextHolder;
import xyz.chener.zp.system.entity.InstanceBaseHealth;
import xyz.chener.zp.system.entity.NacosServerInstance;
import xyz.chener.zp.system.entity.ServerNames;
import xyz.chener.zp.system.entity.dto.InstanceDto;
import xyz.chener.zp.system.service.ActuatorRequest;
import xyz.chener.zp.system.service.NacosRequest;
import xyz.chener.zp.system.service.SystemInfoSerivce;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/16:54
 * @Email: chen@chener.xyz
 */

@Service
public class SystemInfoSerivceImpl implements SystemInfoSerivce {

    private final NacosRequest nacosRequest;

    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    private final ActuatorRequest actuatorRequest;

    private final CommonConfig commonConfig;

    public SystemInfoSerivceImpl(NacosRequest nacosRequest, NacosDiscoveryProperties nacosDiscoveryProperties
            , ActuatorRequest actuatorRequest, CommonConfig commonConfig) {
        this.nacosRequest = nacosRequest;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        this.actuatorRequest = actuatorRequest;
        this.commonConfig = commonConfig;
    }


    @Override
    public List<InstanceDto> getInstances(InstanceDto dto) {
        ServerNames serviceNameList = nacosRequest.getServiceNameList(nacosDiscoveryProperties.getUsername(), nacosDiscoveryProperties.getPassword()
                , 1, 100, nacosDiscoveryProperties.getGroup()
                , nacosDiscoveryProperties.getNamespace());
        List<InstanceDto> hosts = new ArrayList<>();
        serviceNameList.getDoms().forEach(serviceName -> {
            NacosServerInstance instanceList = nacosRequest.getInstanceList(nacosDiscoveryProperties.getUsername(), nacosDiscoveryProperties.getPassword()
                    , serviceName, nacosDiscoveryProperties.getGroup(), nacosDiscoveryProperties.getNamespace());
            if (instanceList != null) {
                instanceList.getHosts().forEach(host -> {
                    InstanceDto instanceDto = new InstanceDto();
                    instanceDto.setHosts(host);
                    instanceDto.setName(instanceList.getName());
                    instanceDto.setGroupName(instanceList.getGroupName());
                    instanceDto.setNamespace(nacosDiscoveryProperties.getNamespace());
                    instanceDto.setLastRefTime(instanceList.getLastRefTime());
                    hosts.add(instanceDto);
                });
            }
        });
        return hosts.stream().filter(h->{
            if (StringUtils.hasText(dto.getName()) && !h.getName().contains(dto.getName())) {
                return false;
            }
            if (StringUtils.hasText(dto.getGroupName()) && !h.getGroupName().contains(dto.getGroupName())) {
                return false;
            }
            if (StringUtils.hasText(dto.getNamespace()) && !h.getNamespace().contains(dto.getNamespace())) {
                return false;
            }
            return true;
        }).toList();
    }

    @Override
    public List<InstanceBaseHealth> getInstanceInfo(String url) {
        List<InstanceBaseHealth> res = new ArrayList<>();
        try {
            HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
            String healthBaseInfo = actuatorRequest.getHealthBaseInfo(commonConfig.getSecurity().getFeignCallSlat());
            ObjectMapper om = new ObjectMapper();
            Map map = om.readValue(healthBaseInfo, Map.class);
            Map components = (Map) map.get("components");
            if (components.containsKey("db")) {
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("数据库:"+getMapValue(components,"db.details.database"));
                instanceBaseHealth.setStatus(getMapValue(components,"db.status"));
                res.add(instanceBaseHealth);
            }
            if (components.containsKey("diskSpace")) {
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                double total = Long.parseLong(getMapValue(components,"diskSpace.details.total"))/1024/1024/1024L;
                double free = Long.parseLong(getMapValue(components,"diskSpace.details.free"))/1024/1024/1024L;
                instanceBaseHealth.setName("磁盘空间");
                instanceBaseHealth.setStatus(String.format("%.3fG/%.3fG",free,total));
                res.add(instanceBaseHealth);
            }
            if (components.containsKey("nacosDiscovery")) {
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("注册中心");
                instanceBaseHealth.setStatus(getMapValue(components,"nacosDiscovery.status"));
                res.add(instanceBaseHealth);
            }
            if (components.containsKey("redis")) {
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("Redis:"+getMapValue(components,"redis.details.version"));
                instanceBaseHealth.setStatus(getMapValue(components,"redis.status"));
                res.add(instanceBaseHealth);
            }
            if (components.containsKey("ping")) {
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("PING");
                instanceBaseHealth.setStatus(getMapValue(components,"ping.status"));
                res.add(instanceBaseHealth);
            }
            if (components.containsKey("rabbit")) {
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("RabbitMQ:"+getMapValue(components,"rabbit.details.version"));
                instanceBaseHealth.setStatus(getMapValue(components,"rabbit.status"));
                res.add(instanceBaseHealth);
            }
            if (components.containsKey("sentinel")) {
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("Sentinel");
                instanceBaseHealth.setStatus(getMapValue(components,"sentinel.status"));
                res.add(instanceBaseHealth);
            }

            runIgnoredError(()->{
                // sys cpu占用
                HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
                String syscpu = actuatorRequest.getMetrics(commonConfig.getSecurity().getFeignCallSlat()
                        , "system.cpu.usage");
                Map map0 = om.readValue(syscpu, Map.class);
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("系统cpu占用");
                String val = getMapValue(map0, "measurements.0.value");
                instanceBaseHealth.setStatus(String.format("%.6f%%", Double.parseDouble(val) * 100));
                res.add(instanceBaseHealth);
                return null;
            });

            runIgnoredError(()->{
                // jvm cpu占用
                HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
                String syscpu = actuatorRequest.getMetrics(commonConfig.getSecurity().getFeignCallSlat()
                        , "process.cpu.usage");
                Map map0 = om.readValue(syscpu, Map.class);
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("jvm最近cpu占用");
                String val = getMapValue(map0, "measurements.0.value");
                instanceBaseHealth.setStatus(String.format("%.6f%%", Double.parseDouble(val) * 100));
                res.add(instanceBaseHealth);
                return null;
            });
            runIgnoredError(()->{
                // 进程运行时间
                HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
                String syscpu = actuatorRequest.getMetrics(commonConfig.getSecurity().getFeignCallSlat()
                        , "process.uptime");
                Map map0 = om.readValue(syscpu, Map.class);
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("进程运行时间");
                String val = getMapValue(map0, "measurements.0.value");
                instanceBaseHealth.setStatus(String.format("%s 秒", val));
                res.add(instanceBaseHealth);
                return null;
            });
            runIgnoredError(()->{
                // jvm 最大内存占用
                HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
                String syscpu = actuatorRequest.getMetrics(commonConfig.getSecurity().getFeignCallSlat()
                        , "jvm.memory.max");
                Map map0 = om.readValue(syscpu, Map.class);
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("jvm 内存占用");
                String val = getMapValue(map0, "measurements.0.value");
                BigDecimal l = new BigDecimal(val).divide(new BigDecimal(1024 * 1024));

                // jvm 内存占用
                HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
                syscpu = actuatorRequest.getMetrics(commonConfig.getSecurity().getFeignCallSlat()
                        , "jvm.memory.used");
                map0 = om.readValue(syscpu, Map.class);
                val = getMapValue(map0, "measurements.0.value");
                BigDecimal l2 = new BigDecimal(val).divide(new BigDecimal(1024 * 1024));

                instanceBaseHealth.setStatus(String.format("%.3fM / %.3fM ",l2, l));
                res.add(instanceBaseHealth);
                return null;
            });
            runIgnoredError(()->{
                // jvm 线程数
                HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
                String syscpu = actuatorRequest.getMetrics(commonConfig.getSecurity().getFeignCallSlat()
                        , "jvm.threads.live");
                Map map0 = om.readValue(syscpu, Map.class);
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("jvm 线程数(live / daemon)");
                String val = getMapValue(map0, "measurements.0.value");
                BigDecimal i = new BigDecimal(val);
                HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
                syscpu = actuatorRequest.getMetrics(commonConfig.getSecurity().getFeignCallSlat()
                        , "jvm.threads.daemon");
                map0 = om.readValue(syscpu, Map.class);
                val = getMapValue(map0, "measurements.0.value");
                BigDecimal i2 = new BigDecimal(val);
                instanceBaseHealth.setStatus(String.format("%s / %s",i, i2));
                res.add(instanceBaseHealth);
                return null;
            });
            runIgnoredError(()-> {
                // 文件句柄数量
                HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
                String syscpu = actuatorRequest.getMetrics(commonConfig.getSecurity().getFeignCallSlat()
                        , "process.files.open");
                Map map0 = om.readValue(syscpu, Map.class);
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("进程占用文件数");
                String val = getMapValue(map0, "measurements.0.value");
                instanceBaseHealth.setStatus(String.format("%s", Double.parseDouble(val)));
                res.add(instanceBaseHealth);
                return null;
            });

        } catch (Exception e) { }
        return res;
    }

    @Override
    public Map getSentinelInfo(String url, String resourceName) {
        HttpRequestContextHolder.setNextBaseUrl(String.format("http://%s", url));
        return actuatorRequest.getSentinelCustom(commonConfig.getSecurity().getFeignCallSlat(), resourceName);
    }


    private String getMapValue(Map map,String key){
       try {
           String[] split = key.split("\\.");
           Object obj = map;
           for (int i = 0; i < split.length; i++) {
               if (i == split.length - 1) {
                   return getMOL(obj, split[i]).toString();
               }
                obj = getMOL(obj, split[i]);
           }
       }catch (Exception e){}
        return null;
    }

    private Integer isNumber(String str){
        try {
            return Integer.parseInt(str);
        }catch (Exception e){}
        return null;
    }
    private Object getMOL(Object o,String k){
        try {
            if (o instanceof Map){
                return ((Map) o).get(k);
            }else if (o instanceof List){
                return ((List) o).get(isNumber(k));
            }
        }catch (Exception e){}
        return "";
    }

    private void runIgnoredError(Callable callable) {
        try {
            callable.call();
        } catch (Exception ignored) {  }
    }

}
