package xyz.chener.zp.workflow.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
public class Json {

    public static class JsonHandle {
        private Object sourceObject;
        private JsonHandle(String json) {
            ObjectMapper om = new ObjectMapper();
            try {
                sourceObject = om.readValue(json, Object.class);
            } catch (Exception e) {
                log.warn("json 解析异常", e);
            }
        }
        private JsonHandle(Object obj){
            ObjectMapper om = new ObjectMapper();
            try {
                var json = om.writeValueAsString(obj);
                sourceObject = om.readValue(json, Object.class);
            } catch (JsonProcessingException e) {
                log.warn("json 解析异常", e);
            }
        }

        public JsonHandle(){}
    }

    public static JsonHandle parse(String json) {
        return new JsonHandle(json);
    }

    public static JsonHandle parse(Object obj) {
        return new JsonHandle(obj);
    }

    public static Object get(String key,JsonHandle handle){
        List<String> list = Arrays.stream(key.split("\\.")).filter(StringUtils::hasText).toList();
        StringBuilder sb = new StringBuilder();
        AtomicReference<Object> res = new AtomicReference<>(handle.sourceObject);
        try {
            list.forEach(e->{
                sb.append(e).append(".");
                if (isArray(e)) {
                    res.set(((List<?>) res.get()).get(getArrayIndex(e)));
                }else {
                    res.set(((java.util.Map<?, ?>) res.get()).get(e));
                }
            });
        }catch (Exception exception){
            if (sb.length() > 1){
                sb.deleteCharAt(sb.length()-1);
            }
            log.error("Json 获取失败, key: {}  --> {}", sb ,exception.getMessage());
            return null;
        }
        return res.get();
    }


    public static boolean set(String key,Object obj,JsonHandle handle) throws RuntimeException {
        List<String> list = Arrays.stream(key.split("\\.")).filter(StringUtils::hasText).toList();
        StringBuilder sb = new StringBuilder();
        var cacheObj = handle.sourceObject;
        try {
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i)).append(".");

                if (isArray(list.get(i))) {
                    if (cacheObj == null){
                        if (i == list.size()-1) {
                            cacheObj = putArrayIndex(cacheObj, obj, getArrayIndex(list.get(i)));
                            if (i == 0){
                                handle.sourceObject = cacheObj;
                            }
                        }else{
                            cacheObj = putArrayIndex(cacheObj, isArray(list.get(i+1))?new ArrayList<>():new LinkedHashMap<>(), getArrayIndex(list.get(i)));
                            if (i == 0){
                                handle.sourceObject = cacheObj;
                            }
                            cacheObj = ((List)cacheObj).get(getArrayIndex(list.get(i)));
                        }
                    } else{
                        if (i == list.size()-1) {
                            cacheObj = putArrayIndex(cacheObj, obj, getArrayIndex(list.get(i)));
                        }else {
                            Object ls = null;
                            try{
                                ls = ((List)cacheObj).get(getArrayIndex(list.get(i)));
                            }catch (Exception ignored){}
                            if (ls == null) {
                                cacheObj = putArrayIndex(cacheObj, isArray(list.get(i + 1)) ? new ArrayList<>() : new LinkedHashMap<>(), getArrayIndex(list.get(i)));
                                cacheObj = ((List) cacheObj).get(getArrayIndex(list.get(i)));
                            } else {
                                cacheObj = ls;
                            }
                        }
                    }
                }else {
                    if (cacheObj == null) {
                        if (i == list.size()-1) {
                            cacheObj = putMap(cacheObj, obj, list.get(i));
                            if (i == 0){
                                handle.sourceObject = cacheObj;
                            }
                        }else{
                            cacheObj = putMap(cacheObj, isArray(list.get(i+1))?new ArrayList<>():new LinkedHashMap<>(), list.get(i));
                            if (i == 0){
                                handle.sourceObject = cacheObj;
                            }
                            cacheObj = ((java.util.Map)cacheObj).get(list.get(i));
                        }
                    } else {
                        if (i == list.size()-1) {
                            cacheObj = putMap(cacheObj, obj, list.get(i));
                        }else {
                            var ls = ((java.util.Map)cacheObj).get(list.get(i));
                            if (ls == null) {
                                cacheObj = putMap(cacheObj, isArray(list.get(i+1))?new ArrayList<>():new LinkedHashMap<>(), list.get(i));
                                cacheObj = ((java.util.Map)cacheObj).get(list.get(i));
                            }else {
                                cacheObj = ls;
                            }
                        }
                    }
                }
            }
        }catch (Exception exception){
            if (sb.length() > 1){
                sb.deleteCharAt(sb.length()-1);
            }
            log.error("Json 设置失败, key: {} --> {}", sb,exception.getMessage());
            return false;
        }
        return true;
    }


    public static String toJson(JsonHandle handle){
        try {
            return new ObjectMapper().writeValueAsString(handle.sourceObject);
        } catch (JsonProcessingException e) {
            return null;
        }
    }


    private static boolean isArray(String key){
        return key.matches("\\[\\d+\\]");
    }

    private static int getArrayIndex(String key){
        return Integer.parseInt(key.substring(1,key.length()-1));
    }

    private static Object putArrayIndex(Object sourceList,Object obj,int index){
        if (!(sourceList instanceof List)){
            sourceList = new ArrayList<>();
        }
        List list = (List) sourceList;
        if (list.size() == index){
            list.add(obj);
        }
        if (list.size() < index){
            for (int i = list.size(); i < index; i++) {
                list.add(null);
            }
            list.add(obj);
        }
        if (list.size() > index){
            list.set(index,obj);
        }
        return sourceList;
    }

    private static Object putMap(Object sourceMap,Object obj,String key){
        if (!(sourceMap instanceof java.util.Map)){
            sourceMap = new java.util.LinkedHashMap<>();
        }
        java.util.Map map = (java.util.Map) sourceMap;
        map.put(key,obj);
        return sourceMap;
    }

}
