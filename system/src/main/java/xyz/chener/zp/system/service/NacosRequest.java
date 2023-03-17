package xyz.chener.zp.system.service;

import org.springframework.web.bind.annotation.RequestHeader;
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

@HttpExchange(NacosRequest.NACOS_HOST)
public interface NacosRequest {

    String NACOS_HOST = "http://101.42.12.133:8848";


    /**
     * 获取服务名列表
     * @param pageNo
     * @param pageSize
     * @param groupName
     * @param namespaceId
     * @return
     */
    @GetExchange("/nacos/v1/ns/service/list")
    ServerNames getServiceNameList(@RequestHeader("username") String username
            , @RequestHeader("password") String password
            ,@RequestParam("pageNo") int pageNo
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
    NacosServerInstance getInstanceList(@RequestHeader("username") String username
            , @RequestHeader("password") String password
            ,@RequestParam("serviceName") String serviceName
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
    ServerInstanceInfo getInstance(@RequestHeader("username") String username
            , @RequestHeader("password") String password
            , @RequestParam("serviceName") String serviceName
            , @RequestParam("groupName") String groupName
            , @RequestParam("namespaceId") String namespaceId
            , @RequestParam("ip") String ip
            , @RequestParam("port") int port);


}
