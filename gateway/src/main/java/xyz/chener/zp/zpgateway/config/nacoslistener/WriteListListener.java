package xyz.chener.zp.zpgateway.config.nacoslistener;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class WriteListListener implements InstanceChangeInterface {

    private final String WRITE_LIST_KEY = "WRITE_LISTS";
    private final String WRITE_LIST_DIVISION = "####";

    public static ConcurrentHashMap<String, CopyOnWriteArrayList<String>> writeListMap = new ConcurrentHashMap<>();


    @Override
    public void onChange(List<Instance> instances,String instanceName, RouteDefinition route) {
        if (instances.isEmpty()){
            CopyOnWriteArrayList<String> m = writeListMap.get(instanceName);
            if (Objects.nonNull(m))
                m.clear();
            writeListMap.remove(instanceName);
        }else {

            int i = new Random().nextInt(instances.size());
            Instance instance = instances.get(i);
            String s = Optional.ofNullable(instance.getMetadata().get(WRITE_LIST_KEY)).orElse("");
            List<String> l = Arrays.stream(s.split(WRITE_LIST_DIVISION))
                    .filter(StringUtils::hasText).toList();
            CopyOnWriteArrayList<String> urls = new CopyOnWriteArrayList<>();

            route.getPredicates().forEach(pd->{
                Map<String, String> uarg = pd.getArgs();
                if (Objects.nonNull(uarg))
                {
                    uarg.values().forEach(eus->{
                        l.forEach(s1 -> {
                            urls.add(eus.replace("/**",s1));
                        });
                    });
                }
            });
            writeListMap.put(instanceName,urls);
        }
    }


}
