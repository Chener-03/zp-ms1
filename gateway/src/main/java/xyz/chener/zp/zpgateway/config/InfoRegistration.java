package xyz.chener.zp.zpgateway.config;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointAutoConfiguration;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.UUID;


@Configuration
public class InfoRegistration implements EnvironmentAware {

    public static final String APP_UID = "application.uid";

    static {
        System.setProperty(APP_UID, UUID.randomUUID().toString().replace("-", ""));
    }

    private Environment environment;

    @Bean
    public InfoEndpoint infoEndpoint(ObjectProvider<InfoContributor> infoContributors) {
        ArrayList<InfoContributor> list = new ArrayList<>(infoContributors.orderedStream().toList());
        list.add(builder -> {
            builder.withDetail(APP_UID, System.getProperty(APP_UID));
            builder.withDetail("spring.application.name", environment.getProperty("spring.application.name"));
        });
        return new InfoEndpoint(list);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
