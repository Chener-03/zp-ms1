package xyz.chener.zp.zpgateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import xyz.chener.zp.sentinelAdapter.nacosclient.SentinelConfigChangeListener;
import xyz.chener.zp.zpgateway.common.entity.R;

import java.util.*;

/**
 * @Author: chenzp
 * @Date: 2023/01/13/15:05
 * @Email: chen@chener.xyz
 */

@Configuration
public class GatewaySentinelConfig implements ApplicationListener<ApplicationStartedEvent> {


    private final ServerCodecConfigurer serverCodecConfigurer;
    private final List<ViewResolver> viewResolvers;


    public GatewaySentinelConfig(ServerCodecConfigurer serverCodecConfigurer
            , ObjectProvider<List<ViewResolver>> viewResolversProvider) {
        this.serverCodecConfigurer = serverCodecConfigurer;
        viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        SentinelConfigChangeListener.flowChangeListeners.add(this::loadFlowRules);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelFilter()
    {
        return new SentinelGatewayFilter();
    }


    public void initBlockHandlers() {
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable)
                -> ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).
                body(BodyInserters.fromValue(R.Builder.getInstance()
                        .setCode(R.HttpCode.HTTP_LIMIT.get())
                        .setMessage(R.ErrorMessage.HTTP_LIMIT.get())
                        .build().toMap()));
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

    public synchronized void loadFlowRules(List<FlowRule> flowRules) {
        List<FlowRule> rule = new ArrayList<>(FlowRuleManager.getRules()
                .stream().filter(e-> flowRules.stream().noneMatch(f -> f.getResource().equals(e.getResource())))
                .toList());
        rule.addAll(flowRules);
        FlowRuleManager.loadRules(rule);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        initBlockHandlers();
    }
}
