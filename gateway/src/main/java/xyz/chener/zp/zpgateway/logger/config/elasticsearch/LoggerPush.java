package xyz.chener.zp.zpgateway.logger.config.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xyz.chener.zp.zpgateway.logger.logback.LogPushEsAppender;
import xyz.chener.zp.zpgateway.logger.logback.entity.LogEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/09:29
 * @Email: chen@chener.xyz
 */

@Component
@Slf4j
public class LoggerPush  implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${zplogger.push.es.host}")
    private String esHost;

    @Value("${zplogger.push.es.port}")
    private String exPort;

    @Value("${zplogger.push.es.username}")
    private String esUsername;

    @Value("${zplogger.push.es.password}")
    private String esPassword;

    @Value("${zplogger.push.es.indexName}")
    private String esIndexName;


    private RestClient restClient;
    private ElasticsearchAsyncClient asyncClient;


    public RestClient getRestClient() {
        return restClient;
    }

    public ElasticsearchAsyncClient getAsyncClient() {
        return asyncClient;
    }

    private void init() {
        if (StringUtils.hasText(esHost)) {
            if (StringUtils.hasText(esUsername)) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY
                        , new UsernamePasswordCredentials(esUsername, esPassword));
                restClient = RestClient.builder(
                                new HttpHost(esHost, Integer.parseInt(exPort)))
                        .setHttpClientConfigCallback(h -> {
                            h.setDefaultCredentialsProvider(credentialsProvider);
                            return h;
                        })
                        .build();
            } else {
                restClient = RestClient.builder(
                                new HttpHost(esHost, Integer.parseInt(exPort)))
                        .build();
            }
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());
            asyncClient = new ElasticsearchAsyncClient(transport);
            checkIndex();
            LogPushEsAppender.initQueue(this);
        }
    }

    private void checkIndex() {
        try {
            asyncClient.indices().exists(builder -> {
                builder.index(esIndexName);
                return builder;
            }).whenComplete((r, e) -> {
                if (e != null) {
                    log.error("check index error", e);
                } else {
                    if (!r.value()){
                        try {
                            asyncClient.indices().create(build -> {
                                build.index(esIndexName)
                                        .settings(builder1 -> builder1.numberOfShards(String.valueOf(1))
                                                .numberOfReplicas(String.valueOf(1)));
                                return build;
                            }).whenComplete((r1, e1) -> {
                                if (e1 != null) {
                                    log.error("创建Log索引失败", e1);
                                } else {
                                    log.info("创建Log索引成功:" + esIndexName);
                                }
                            });
                        } catch (IOException ex) {
                        }
                    }
                }
            });
        } catch (Exception ignored) {
        }
    }


    public void add(ArrayList<LogEntity> logs,Runnable callback) {
        try {
            List<BulkOperation> bulk = new ArrayList<>();
            logs.forEach(l -> bulk.add(new BulkOperation.Builder()
                    .index(be -> be.index(esIndexName).document(l)).build()));
            callback.run();

            asyncClient.bulk(bu-> bu.index(esIndexName)
                    .operations(bulk)).whenComplete((r,e)->{
                if (e != null) {
                    log.error("推送异常{},可能丢失{}条数据:", e, bulk.size());
                }
            });
        } catch (Exception ignored) { }
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        esHost = event.getApplicationContext().getEnvironment().getProperty("zplogger.push.es.host");
        exPort = event.getApplicationContext().getEnvironment().getProperty("zplogger.push.es.port");
        esUsername = event.getApplicationContext().getEnvironment().getProperty("zplogger.push.es.username");
        esPassword = event.getApplicationContext().getEnvironment().getProperty("zplogger.push.es.password");
        esIndexName = event.getApplicationContext().getEnvironment().getProperty("zplogger.push.es.indexName");
        init();
    }
}
