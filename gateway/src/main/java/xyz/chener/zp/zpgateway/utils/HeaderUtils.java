package xyz.chener.zp.zpgateway.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;

public class HeaderUtils {

    public static ServerWebExchange addReactiveHeader(ServerWebExchange exchange,String key,String value,String ... more)
    {
        ServerHttpRequest oldRequest = exchange.getRequest();
        ServerHttpRequest newRequest = oldRequest.mutate().build();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        ArrayList<String> list = new ArrayList<>();
        list.add(value);
        headers.put(key,list);
        if (more.length>1 && more.length % 2 == 0)
        {
            for (int i = 0; i < more.length; i+=2) {
                ArrayList<String> list2 = new ArrayList<>();
                list2.add(more[i+1]);
                headers.put(more[i],list2);
            }
        }
        newRequest = new ServerHttpRequestDecorator(newRequest) {
            @Override
            public HttpHeaders getHeaders() {
                return headers;
            }
        };
        return exchange.mutate().request(newRequest).build();
    }




}
