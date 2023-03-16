package xyz.chener.zp.system.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import xyz.chener.zp.system.entity.NacosServerInstance;
import xyz.chener.zp.system.entity.ServerInstanceInfo;
import xyz.chener.zp.system.entity.ServerNames;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/16:57
 * @Email: chen@chener.xyz
 */

@HttpExchange("${spring.cloud.nacos.discovery.server-addr}")
public interface NacosRequest {

    /**
     * 获取服务名列表
     * @param pageNo
     * @param pageSize
     * @param groupName
     * @param namespaceId
     * @return
     */
    @GetExchange("/nacos/v1/ns/service/list")
    ServerNames getServiceNameList(@RequestParam("pageNo") int pageNo
            , @RequestParam("pageSize") int pageSize
            , @RequestParam("groupName") String groupName
            , @RequestParam("namespaceId") String namespaceId);


    /**
     * 按照服务名获取实例列表
     * @param serviceName
     * @param groupName
     * @param namespaceId
     * @return
     */
    @GetExchange("/nacos/v1/ns/instance/list")
    NacosServerInstance getInstanceList(@RequestParam("serviceName") String serviceName
            , @RequestParam("groupName") String groupName
            , @RequestParam("namespaceId") String namespaceId);


    /**
     * 获取实例信息
     * @param serviceName
     * @param groupName
     * @param namespaceId
     * @param ip
     * @param port
     * @return
     */
    @GetExchange("/nacos/v1/ns/instance")
    ServerInstanceInfo getInstance(@RequestParam("serviceName") String serviceName
            , @RequestParam("groupName") String groupName
            , @RequestParam("namespaceId") String namespaceId
            , @RequestParam("ip") String ip
            , @RequestParam("port") int port);


}
