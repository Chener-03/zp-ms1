package xyz.chener.zp.common.config.grayscalePublishing;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.feign.loadbalance.LoadBalanceDispatch;
import xyz.chener.zp.common.config.nacosMetadataReg.MetatadaRegInterface;

import java.util.Map;



public class GrayscalePublishTagRegister implements MetatadaRegInterface, EnvironmentAware {

    private static Environment environment = null;

    @Override
    public void registerMetadata(Map<String, String> map) {
        if (environment != null && StringUtils.hasText(environment.getProperty("zp.instance-tag"))){
            map.put(LoadBalanceDispatch.HTTP_TAG_HEADER,environment.getProperty("zp.instance-tag"));
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        GrayscalePublishTagRegister.environment = environment;
    }
}
