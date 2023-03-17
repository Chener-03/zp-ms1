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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static class Target{
        public static final String SERVICE = "service";
    }


    public void getInstanceInfo(String addr,String target){
        HttpRequestContextHolder.setNextBaseUrl("http://127.0.0.1:6168");
        String healthBaseInfo = actuatorRequest.getHealthBaseInfo(commonConfig.getSecurity().getFeignCallSlat());
        ObjectMapper om = new ObjectMapper();
        try {
            Map map = om.readValue(healthBaseInfo, Map.class);

            List<InstanceBaseHealth> l = new ArrayList<>();
            Map components = (Map) map.get("components");
            if (components.containsKey("db")) {
                Map db = (Map) components.get("db");
                String dbName = ((Map) db.get("details")).get("database").toString();
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("数据库:"+dbName);
                instanceBaseHealth.setStatus(db.get("status").toString());
                l.add(instanceBaseHealth);
            }
            if (components.containsKey("diskSpace")) {
                Map diskSpace = (Map) components.get("diskSpace");
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                Map details = (Map)diskSpace.get("details");
                double total = Long.parseLong(details.get("total").toString())/1024/1024/1024L;
                double free = Long.parseLong(details.get("free").toString())/1024/1024/1024L;
                instanceBaseHealth.setName(String.format("磁盘空间:%sG/%sG",free,total));
                instanceBaseHealth.setStatus(diskSpace.get("status").toString());
                l.add(instanceBaseHealth);
            }
            if (components.containsKey("nacosDiscovery")) {
                Map nacosDiscovery = (Map) components.get("nacosDiscovery");
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("注册中心");
                instanceBaseHealth.setStatus(nacosDiscovery.get("status").toString());
                l.add(instanceBaseHealth);
            }
            if (components.containsKey("redis")) {
                Map redis = (Map) components.get("redis");
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                String version = ((Map) redis.get("details")).get("version").toString();
                instanceBaseHealth.setName("Redis:"+version);
                instanceBaseHealth.setStatus(redis.get("status").toString());
                l.add(instanceBaseHealth);
            }
            if (components.containsKey("ping")) {
                Map ping = (Map) components.get("ping");
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                instanceBaseHealth.setName("PING");
                instanceBaseHealth.setStatus(ping.get("status").toString());
                l.add(instanceBaseHealth);
            }
            if (components.containsKey("rabbit")) {
                Map rabbit = (Map) components.get("rabbit");
                InstanceBaseHealth instanceBaseHealth = new InstanceBaseHealth();
                String version = ((Map) rabbit.get("details")).get("version").toString();
                instanceBaseHealth.setName("RabbitMQ:"+version);
                instanceBaseHealth.setStatus(rabbit.get("status").toString());
                l.add(instanceBaseHealth);
            }


            System.out.println();
        } catch (JsonProcessingException e) { }

        System.out.println();
    }




}
