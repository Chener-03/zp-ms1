package xyz.chener.zp.common.config.feign;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.entity.CommonVar;

public class FeignRequestInterceptor  implements RequestInterceptor {
    private final CommonConfig commonConfig;

    public FeignRequestInterceptor(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(CommonVar.OPEN_FEIGN_HEADER, commonConfig.getSecurity().getFeignCallSlat());
    }
}
