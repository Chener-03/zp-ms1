package xyz.chener.zp.zpgateway.config.nacoslistener;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

public interface InstanceChangeInterface {
    default void onChange(List<Instance> instances, String instanceName, RouteDefinition route) {
    }
}
