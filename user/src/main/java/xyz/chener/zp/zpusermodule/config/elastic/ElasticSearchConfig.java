package xyz.chener.zp.zpusermodule.config.elastic;


import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.util.ObjectBuilder;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ElasticSearchConfig implements CommandLineRunner {


    @Override
    public void run(String... args) throws Exception {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "Abcd1234...."));


        RestClient restClient = RestClient.builder(
                new HttpHost("101.42.12.133", 9200))
                .setHttpClientConfigCallback(h->{
                    h.setDefaultCredentialsProvider(credentialsProvider);
                    return h;
                })
                .build();




        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());


        ElasticsearchClient client = new ElasticsearchClient(transport);
        ElasticsearchAsyncClient asyncClient = new ElasticsearchAsyncClient(transport);

        asyncClient.indices().exists(builder -> {
            builder.index("zpLogs");
            return builder;
        }).whenComplete((r,e)->{
            if (e != null) {
                e.printStackTrace();
            } else {
                System.out.println(r);
            }
        });


        BooleanResponse zpLogs = client.indices().exists(builder -> {
            builder.index("zpLogs");
            return builder;
        });


        /*client.indices().create(builder -> {
            builder.index("zpLogs")
                    .settings(builder1 -> builder1.numberOfShards(String.valueOf(1))
                            .numberOfReplicas(String.valueOf(1)));
            return builder;
        });*/

        System.out.println();
    }
}
