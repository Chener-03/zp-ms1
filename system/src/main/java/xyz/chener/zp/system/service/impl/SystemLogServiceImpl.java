package xyz.chener.zp.system.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
            Function<Query.Builder, ObjectBuilder<Query>> queryBuilder = new Function<>() {
                @Override
                public ObjectBuilder<Query> apply(Query.Builder builder1) {
                    return builder1.bool(b -> {
                        if (StringUtils.hasText(dto.getLevel())) {
                            b.must(m -> {
                                m.term(t -> {
                                    t.field("level.keyword");
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
                }
            };

            CountResponse countResponse = asyncClient.count(cbd -> cbd.index(commonConfig.getLoggerPush().getEsIndexName())
                    .query(queryBuilder)).get();

            SearchResponse<LogEntityDto> response = asyncClient.search(builder -> {
                builder.index(commonConfig.getLoggerPush().getEsIndexName())
                        .query(queryBuilder)
                        .sort(b -> b.field(sfd -> sfd.field("time.keyword").order(SortOrder.Desc)))
                        .from((page-1)*size).size(size);
                return builder;
            }, LogEntityDto.class).get();



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
}
