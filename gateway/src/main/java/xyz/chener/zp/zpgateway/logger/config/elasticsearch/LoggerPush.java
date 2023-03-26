package xyz.chener.zp.zpgateway.logger.config.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xyz.chener.zp.zpgateway.logger.logback.LogPushEsAppender;
import xyz.chener.zp.zpgateway.logger.logback.entity.LogEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    @Value("${zplogger.push.es.retryCount:3}")
    private Integer retryCount;

    @Value("${zplogger.push.es.failPath:./logs/logger-push-fail.log}")
    private String failPath;


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
        String indexPattnerName = esIndexName + "-*";
        try {
            Request req = new Request("GET", "_index_template/zplogs-template");
            restClient.performRequest(req);
        } catch (ResponseException responseException) {
            if (responseException.getResponse().getStatusLine().getStatusCode() == 404) {
                try(InputStream resource = this.getClass().getResourceAsStream("/log_index_template.json")) {
                    ByteArrayOutputStream bios = new ByteArrayOutputStream();
                    resource.transferTo(bios);
                    ObjectMapper om = new ObjectMapper();
                    Map map = om.readValue(bios.toString(StandardCharsets.UTF_8), Map.class);
                    map.put("index_patterns", indexPattnerName);
                    Request request = new Request("PUT","_index_template/zplogs-template" );
                    request.setJsonEntity(om.writeValueAsString(map));
                    restClient.performRequest(request);
                }catch (Exception exception){
                    log.error("日志创建索引模板失败:{}", exception.getMessage());
                }
            }
        } catch (Exception exception){
            log.error("日志索引检查失败:{}", exception.getMessage());
        }
    }


    public void add(ArrayList<LogEntity> logs,Runnable callback) {
        String indexName = esIndexName + "-" + new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        try {
            List<BulkOperation> bulk = new ArrayList<>();
            logs.forEach(l -> bulk.add(new BulkOperation.Builder()
                    .index(be -> be.index(indexName).document(l)).build()));
            callback.run();

            asyncClient.bulk(bu-> bu.index(indexName)
                    .operations(bulk)).whenComplete((r,e)->{
                if (e != null) {
                    CompletableFuture.runAsync(()->{
                        retry(bulk,indexName, retryCount);
                    });
                }
            });
        } catch (Exception ignored) { }
    }

    public void retry(List<BulkOperation> bulk,String indexName,Integer count)
    {
        try {
            BulkResponse res = asyncClient.bulk(bu -> bu.index(indexName).operations(bulk)).get();
            if (res.errors()) {
                throw new Exception();
            }
        }catch (Exception exception){
            if (count <= 0){
                try {
                    RandomAccessFile randomFile = new RandomAccessFile(failPath, "rw");
                    long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    ObjectMapper om = new ObjectMapper();
                    bulk.forEach(e->{
                        try {
                            randomFile.writeBytes(om.writeValueAsString(e._get()));
                        } catch (IOException ioException) {
                            System.err.println(ioException.getMessage());
                        }
                    });
                    randomFile.close();
                }catch (Exception exc1){
                    log.error("推送失败日志记录失败:{}", exc1.getMessage());
                }
                return;
            }
            retry(bulk, indexName,count - 1);
        }
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
