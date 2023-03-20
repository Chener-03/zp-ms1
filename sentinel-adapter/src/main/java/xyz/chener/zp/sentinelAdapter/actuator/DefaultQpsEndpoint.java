package xyz.chener.zp.sentinelAdapter.actuator;

import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.boot.actuate.info.Info;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/16:18
 * @Email: chen@chener.xyz
 */


@WebEndpoint(id = "sentinelCustom")
public class DefaultQpsEndpoint {

    @ReadOperation
    public Info get(@Selector String resourceNameMatch) {
        Info.Builder builder = new Info.Builder().withDetail("匹配资源名", resourceNameMatch);
        ClusterBuilderSlot.getClusterNodeMap().keySet().stream()
                .filter(e->{
                    if(resourceNameMatch.equals("ALL")){
                        return true;
                    }
                    return e.getName().startsWith(resourceNameMatch);
                })
                .forEach(e->{
                    ClusterNode clusterNode = ClusterBuilderSlot.getClusterNode(e.getName());
                    int resourceType = clusterNode.getResourceType();
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("resourceType", resourceType);
                    map.put("平均响应时间", clusterNode.avgRt());
                    map.put("总请求数", clusterNode.totalRequest());
                    map.put("每分钟拦截的请求个数", clusterNode.blockRequest());
                    map.put("每秒拦截个数", clusterNode.blockQps());
                    map.put("并发个数", clusterNode.curThreadNum());
                    map.put("每秒成功通过请求", clusterNode.passQps());
                    map.put("每秒到来的请求", clusterNode.previousPassQps());
                    map.put("每分钟请求数", clusterNode.totalQps());
                    builder.withDetail(e.getName(), map);
                });
        return builder.build();
    }

}
