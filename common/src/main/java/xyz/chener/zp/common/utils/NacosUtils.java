package xyz.chener.zp.common.utils;


import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.chener.zp.common.config.feign.loadbalance.ServerInstance;

import java.util.Collections;
import java.util.List;

@Slf4j
public class NacosUtils {

    private NacosServiceManager manager;

    public NacosServiceManager getManager() {
        return manager;
    }

    @Autowired(required = false)
    public void setManager(NacosServiceManager manager) {
        this.manager = manager;
    }

    public List<ServerInstance> getServerInstance(String serviceName, String groupName) {
        try {
            List<Instance> allInstances = manager.getNamingService().getAllInstances(serviceName, groupName);
            return allInstances.stream().map(e-> new ServerInstance(e.getIp(),e.getPort())).toList();
        } catch ( Exception e) {
            log.warn("获取服务实例[{}.{}]失败",groupName,serviceName, e.getCause());
            return Collections.EMPTY_LIST;
        }
    }


}
