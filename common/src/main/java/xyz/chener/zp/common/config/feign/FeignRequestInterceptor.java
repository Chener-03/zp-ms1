package xyz.chener.zp.common.config.feign;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.feign.loadbalance.LoadBalanceDispatch;
import xyz.chener.zp.common.entity.CommonVar;
import xyz.chener.zp.common.utils.RequestUtils;
import xyz.chener.zp.common.utils.ThreadUtils;

import java.util.Objects;
import java.util.Optional;

public class FeignRequestInterceptor  implements RequestInterceptor {
    private final CommonConfig commonConfig;

    public FeignRequestInterceptor(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(CommonVar.OPEN_FEIGN_HEADER, commonConfig.getSecurity().getFeignCallSlat());
        ThreadUtils.runIgnoreException(()-> Optional.ofNullable(Objects.requireNonNull(RequestUtils.getConcurrentRequest()).getHeader(LoadBalanceDispatch.HTTP_TAG_HEADER))
                .ifPresent(tag-> template.header(LoadBalanceDispatch.HTTP_TAG_HEADER,tag)));
    }
}
