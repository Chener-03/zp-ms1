package xyz.chener.zp.system.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import co.elastic.clients.util.ObjectBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.entity.vo.PageInfo;
import xyz.chener.zp.logger.config.elasticsearch.LoggerPush;
import xyz.chener.zp.system.entity.dto.LogEntityDto;
import xyz.chener.zp.system.error.LogSearchError;
import xyz.chener.zp.system.service.SystemLogService;
import xyz.chener.zp.system.utils.EsResultUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;


@Service
@Slf4j
public class SystemLogServiceImpl implements SystemLogService {

    private LoggerPush loggerPush;

    private CommonConfig commonConfig;

    @Autowired
    public void setLoggerPush(LoggerPush loggerPush) {
        this.loggerPush = loggerPush;
    }

    @Autowired
    public void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    public PageInfo<LogEntityDto> getAppLogs(LogEntityDto dto, Integer page, Integer size) {
        ElasticsearchAsyncClient asyncClient = loggerPush.getAsyncClient();
        PageInfo<LogEntityDto> res = new PageInfo<>();
        try {
            Function<Query.Builder, ObjectBuilder<Query>> queryBuilder = builder1 -> builder1.bool(b -> {
                if (StringUtils.hasText(dto.getLevel())) {
                    b.must(m -> {
                        m.term(t -> {
                            t.field("level");
                            t.value(FieldValue.of(dto.getLevel()));
                            return t;
                        });
                        return m;
                    });
                }
                if (StringUtils.hasText(dto.getsId())) {
                    b.must(m -> {
                        m.term(t -> {
                            t.field("sId.keyword");
                            t.value(FieldValue.of(dto.getsId()));
                            return t;
                        });
                        return m;
                    });
                }
                if (StringUtils.hasText(dto.getiId())) {
                    b.must(m -> {
                        m.term(t -> {
                            t.field("iId.keyword");
                            t.value(FieldValue.of(dto.getiId()));
                            return t;
                        });
                        return m;
                    });
                }
                if (StringUtils.hasText(dto.getTid())) {
                    b.must(m -> {
                        m.term(t -> {
                            t.field("tId.keyword");
                            t.value(FieldValue.of(dto.getTid()));
                            return t;
                        });
                        return m;
                    });
                }
                if (StringUtils.hasText(dto.getMessage())) {
                    b.must(m -> {
                        m.match(mch -> mch.field("message")
                                .query(que -> que.stringValue(dto.getMessage())));
                        return m;
                    });
                }
                return b;
            });

            String indexNamePrefix = commonConfig.getLoggerPush().getEsIndexName()+"-*";

            CountResponse countResponse = asyncClient.count(cbd -> cbd.index(indexNamePrefix)
                    .query(queryBuilder)).get();

            SearchResponse<LogEntityDto> response = null;

            long l1 = System.currentTimeMillis();

            if ((page-1)*size > 9900){
                List<String> after = deepSearchAfter(indexNamePrefix, "time", new String[]{"time", "uuid"}, queryBuilder, SortOrder.Desc, (page - 1) * size, (int) countResponse.count());
                response = asyncClient.search(builder -> {
                    builder.index(indexNamePrefix)
                            .query(queryBuilder)
                            .searchAfter(after)
                            .from(0).size(size);
                    List<SortOptions> sortOpeionList = Arrays.stream(new String[]{"time", "uuid"}).map(sop -> new SortOptions.Builder().field(sfd -> sfd.field(sop).order(SortOrder.Desc)).build()).toList();
                    builder.sort(sortOpeionList);
                    return builder;
                }, LogEntityDto.class).get();

            }else {
                response = asyncClient.search(builder -> {
                    builder.index(indexNamePrefix)
                            .query(queryBuilder)
                            .sort(b -> b.field(sfd -> sfd.field("time").order(SortOrder.Desc)))
                            .from((page-1)*size).size(size);
                    return builder;
                }, LogEntityDto.class).get();
            }

            long l2 = System.currentTimeMillis() - l1;

            List<LogEntityDto> list = EsResultUtils.getEsResponseList(response);
            res.setTotal(countResponse.count());
            res.setList(list);
            res.setPages((int) Math.ceil((double) countResponse.count() / size));
            res.setPageSize(size);
        } catch (Exception e) {
            throw new LogSearchError(e.getMessage()+e.getCause());
        }
        return res;
    }



    private List<String> deepSearchAfter(String indexName,String include,String[] order,Function<Query.Builder, ObjectBuilder<Query>> query,SortOrder desc,Integer from,Integer count) throws IOException, ExecutionException, InterruptedException {
        ElasticsearchAsyncClient asyncClient = loggerPush.getAsyncClient();
        final int BATCH_SIZE = 10000;
        AtomicReference<List<String>> searchCache = new AtomicReference<>(new ArrayList<>());

        if (from > count){
            throw new LogSearchError("超出最大查询范围");
        }

        for (int i = 0; i < from;) {

            AtomicInteger concurrentBatchSize = new AtomicInteger(0);

            if (i + BATCH_SIZE > from){
                concurrentBatchSize.set(from-i);
                i = from;
            }else {
                concurrentBatchSize.set(BATCH_SIZE);
                i += BATCH_SIZE;
            }

            SearchResponse<Object> resp = asyncClient.search(builder -> {
                builder.index(indexName)
                        .query(query)
                        .source(source -> source.filter(filter -> filter.includes(include)));

                List<SortOptions> sortOpeionList = Arrays.stream(order).map(sop -> new SortOptions.Builder().field(sfd -> sfd.field(sop).order(desc)).build()).toList();
                builder.sort(sortOpeionList);

                if (!searchCache.get().isEmpty()) {
                    builder.searchAfter(searchCache.get()).size(concurrentBatchSize.get());
                } else {
                    builder.from(0).size(concurrentBatchSize.get());
                }
                return builder;
            }, Object.class).get();

            if (resp.hits().hits().isEmpty()){
                throw new LogSearchError("超出最大查询范围");
            }

            searchCache.get().clear();
            int last = resp.hits().hits().size() - 1;
            for (int ct = 0; ct < order.length; ct++) {
                searchCache.get().add(resp.hits().hits().get(last).sort().get(ct));
            }
        }

        return searchCache.get();
    }


}
