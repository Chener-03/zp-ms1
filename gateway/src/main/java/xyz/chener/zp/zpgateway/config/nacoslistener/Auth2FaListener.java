package xyz.chener.zp.zpgateway.config.nacoslistener;


import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.chener.zp.zpgateway.common.entity.R;
import xyz.chener.zp.zpgateway.entity.Auth2FaRegisterMetadata;
import xyz.chener.zp.zpgateway.service.UserModuleService;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.chener.zp.zpgateway.common.entity.CommonVar.FA_HEADER_KEY;


@Slf4j
@Component
public class Auth2FaListener implements InstanceChangeInterface  {

    public static final ConcurrentHashMap<String, CopyOnWriteArrayList<Auth2FaRegisterMetadata>> faUrls = new ConcurrentHashMap<>();

    private final String KEY = "2FA_URL_LIST";
    private final String DIVISION = "####";


    @Override
    public void onChange(List<Instance> instances, String instanceName, RouteDefinition route) {
        if (instances == null || instances.isEmpty()){
            CopyOnWriteArrayList<Auth2FaRegisterMetadata> m = faUrls.get(instanceName);
            if (Objects.nonNull(m))
                m.clear();
            faUrls.remove(instanceName);
        }else {
            int i = new Random().nextInt(instances.size());
            Instance instance = instances.get(i);
            String json = Optional.ofNullable(instance.getMetadata().get(KEY)).orElse("[]");

            List<Auth2FaRegisterMetadata> auth2FaRegisterMetadatas = null;
            try {
                auth2FaRegisterMetadatas = new ObjectMapper().readValue(json, new TypeReference<List<Auth2FaRegisterMetadata>>() {});
            } catch (Exception e) {
                log.warn("2FA_URL_LIST json parse error:{}",e.getMessage());
                auth2FaRegisterMetadatas = new ArrayList<>();
            }

            CopyOnWriteArrayList<Auth2FaRegisterMetadata> mds = new CopyOnWriteArrayList<>();

            final List<Auth2FaRegisterMetadata> finalAuth2FaRegisterMetadatas = auth2FaRegisterMetadatas;

            route.getPredicates().forEach(pd->{
                Map<String, String> uarg = pd.getArgs();
                if (Objects.nonNull(uarg))
                {
                    uarg.values().forEach(eus->{
                        finalAuth2FaRegisterMetadatas.forEach(s1 -> {
                            s1.setUrl(eus.replace("/**",s1.getUrl()));
                            mds.add(s1);
                        });
                    });
                }
            });
            faUrls.put(instanceName,mds);
        }
    }
}
