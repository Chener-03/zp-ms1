package xyz.chener.zp.system.utils;

import co.elastic.clients.elasticsearch.core.SearchResponse;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EsResultUtils {


    public static <T> List<T> getEsResponseList(SearchResponse<T> response) {
        return response.hits().hits().stream().map(h->{
            T res = h.source();
            try {
                Field id = res.getClass().getDeclaredField("id");
                if (id.getType().equals(String.class)){
                    boolean b = id.canAccess(res);
                    id.setAccessible(true);
                    id.set(res, h.id());
                    id.setAccessible(b);
                }
            }catch (Exception exception){}
            return res;
        }).toList();
    }

}
