package xyz.chener.zp.logger.config.elasticsearch;

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
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.logger.logback.LogPushEsAppender;
import xyz.chener.zp.logger.logback.entity.LogEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/09:29
 * @Email: chen@chener.xyz
 */

@Slf4j
public class LoggerPush {


    private final CommonConfig.LoggerPush loggerPush;
    private RestClient restClient;
    private ElasticsearchAsyncClient asyncClient;
    public LoggerPush(CommonConfig commonConfig) {
        this.loggerPush = commonConfig.getLoggerPush();
        init();
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public ElasticsearchAsyncClient getAsyncClient() {
        return asyncClient;
    }

    private void init() {
        if (StringUtils.hasText(loggerPush.getEsHost())) {
            if (StringUtils.hasText(loggerPush.getEsUsername())) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY
                        , new UsernamePasswordCredentials(loggerPush.getEsUsername(), loggerPush.getEsPassword()));
                restClient = RestClient.builder(
                                new HttpHost(loggerPush.getEsHost(), Integer.parseInt(loggerPush.getExPort())))
                        .setHttpClientConfigCallback(h -> {
                            h.setDefaultCredentialsProvider(credentialsProvider);
                            return h;
                        })
                        .build();
            } else {
                restClient = RestClient.builder(
                                new HttpHost(loggerPush.getEsHost(), Integer.parseInt(loggerPush.getExPort())))
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
                builder.index(loggerPush.getEsIndexName());
                return builder;
            }).whenComplete((r, e) -> {
                if (e != null) {
                    log.error("check index error", e);
                } else {
                    if (!r.value()){
                        try {
                            asyncClient.indices().create(build -> {
                                build.index(loggerPush.getEsIndexName())
                                        .settings(builder1 -> builder1.numberOfShards(String.valueOf(1))
                                                .numberOfReplicas(String.valueOf(1)));
                                return build;
                            }).whenComplete((r1, e1) -> {
                                if (e1 != null) {
                                    log.error("创建Log索引失败", e1);
                                } else {
                                    log.info("创建Log索引成功:" + loggerPush.getEsIndexName());
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
                    .index(be -> be.index(loggerPush.getEsIndexName()).document(l)).build()));
            callback.run();

            asyncClient.bulk(bu-> bu.index(loggerPush.getEsIndexName())
                    .operations(bulk)).whenComplete((r,e)->{
                if (e != null) {
                    log.error("推送异常{},可能丢失{}条数据:", e, bulk.size());
                }
            });
        } catch (Exception ignored) { }
    }


}
