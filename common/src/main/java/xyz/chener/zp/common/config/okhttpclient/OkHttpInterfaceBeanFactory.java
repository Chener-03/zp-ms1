package xyz.chener.zp.common.config.okhttpclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.source.tree.Tree;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.*;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import xyz.chener.zp.common.config.okhttpclient.error.OkHttpInterfaceRequestMethodError;
import xyz.chener.zp.common.config.okhttpclient.error.OkHttpInterfaceUrlBuildError;
import xyz.chener.zp.common.config.okhttpclient.error.OkHttpResponseError;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @Author: chenzp
 * @Date: 2023/02/17/08:55
 * @Email: chen@chener.xyz
 */


public class OkHttpInterfaceBeanFactory implements FactoryBean {


    private Class mapperInterface;

    private OkHttpClient http;

    public OkHttpClient getHttp() {
        return http;
    }

    public void setHttp(OkHttpClient http) {
        this.http = http;
    }

    public OkHttpInterfaceBeanFactory() {
    }

    public OkHttpInterfaceBeanFactory(Class mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Slf4j
    private static class RequestJdkProxy implements InvocationHandler, Serializable{

        private static final String GET = "GET";
        private static final String POST = "POST";

        @Data
        private static class RequestMetaData{
            private String url;
            private String method;
            private Map<String,Object> params;
            private String jsonParams;
            private Map<String,String> headers;
            private Map<String,String> pathParams;
        }

        private OkHttpClient client;


        public RequestJdkProxy(OkHttpClient client) {
            this.client = client;
        }

        private RequestMetaData getMethodMetaData(Method method,Object proxyObj,Object[] args){
            RequestMetaData md = new RequestMetaData();
            md.setUrl(getUrl(method));
            GetExchange getAnn = method.getAnnotation(GetExchange.class);
            PostExchange postAnn = method.getAnnotation(PostExchange.class);
            AssertUrils.state(getAnn != null || postAnn != null, OkHttpInterfaceRequestMethodError.class);
            Map<String, String> headers = getHeaders(method, proxyObj,args);
            Map<String, Object> params = getFormDataParams(method, proxyObj, args);
            String jsonParams = getJsonParams(method, proxyObj, args);
            Map<String, String> pathParam = getPathParam(method, proxyObj, args);
            if (getAnn != null) {
                md.setMethod(GET);
            }else {
                md.setMethod(POST);
            }
            md.setHeaders(headers);
            md.setPathParams(pathParam);
            md.setParams(params);
            md.setJsonParams(jsonParams);
            return md;
        }

        private Map<String,String> getHeaders(Method method,Object obj,Object[] args){
            Annotation[][] ann = method.getParameterAnnotations();
            Map<String, String> map = new HashMap<>();
           for (int i = 0; i < ann.length; i++) {
                Annotation[] ans = ann[i];
                for (Annotation a : ans) {
                    if (a.annotationType().getName().equals(RequestHeader.class.getName())) {
                        RequestHeader rh = (RequestHeader) a;
                        map.put(rh.value(), args[i].toString());
                        break;
                    }
                }
            }
            return map;
        }

        private Map<String,String> getPathParam(Method method,Object obj,Object[] args){
            Annotation[][] ann = method.getParameterAnnotations();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < ann.length; i++) {
                Annotation[] ans = ann[i];
                for (Annotation a : ans) {
                    if (a.annotationType().getName().equals(PathVariable.class.getName())) {
                        PathVariable rh = (PathVariable) a;
                        map.put(rh.value(), args[i].toString());
                        break;
                    }
                }
            }
            return map;
        }

        private String safeGetObjectString(Object obj){
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        private Map<String,Object> getFormDataParams(Method method, Object obj, Object[] args){
            Annotation[][] ann = method.getParameterAnnotations();
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < ann.length; i++) {
                Annotation[] ans = ann[i];
                for (Annotation a : ans) {
                    if (a.annotationType().getName().equals(RequestParam.class.getName())) {
                        RequestParam rh = (RequestParam) a;
                        if (args[i] instanceof Collection) {
                            Collection c = (Collection) args[i];
                            c.forEach(e -> {
                                if (!addFormDataParam(map,e,rh.value())) {
                                    getFormDataParamsForObject(map,e,e.getClass());
                                }
                            });
                        }else {
                            if (!addFormDataParam(map,args[i],rh.value())) {
                                getFormDataParamsForObject(map,args[i],args[i].getClass());
                            }
                        }
                        break;
                    }
                }
            }
            return map;
        }

        private void getFormDataParamsForObject(Map<String, Object> sourceMap,Object obj,Class objClazz){
            Arrays.stream(ReflectionUtils.getAllDeclaredMethods(objClazz))
                    .filter(e-> e.getName().indexOf("get")==0
                            && e.getName().length() > 3
                            && e.getModifiers()== Modifier.PUBLIC
                            && e.getParameterCount() == 0
                            && e.getReturnType() != void.class
                            && e.getReturnType() != Void.class)
                    .forEach(e->{
                        Object o = ReflectionUtils.invokeMethod(e, obj);
                        String key = e.getName().substring(3, 4).toLowerCase() + e.getName().substring(4);
                        if (o != null) {
                            if (!addFormDataParam(sourceMap,o,key)) {
                                getFormDataParamsForObject(sourceMap, o, o.getClass());
                            }
                        }
                    });
        }
        private Boolean addFormDataParam(Map<String, Object> sourceMap,Object obj,String key){
            if (ObjectUtils.isBasicType(obj)) {
                sourceMap.put(key, safeGetObjectString(obj));
                return true;
            }
            if (obj instanceof Date date){
                sourceMap.put(key,String.valueOf(date.getTime()));
                return true;
            }
            if (obj instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) obj;
                sourceMap.putAll(m);
                return true;
            }
            return false;
        }

        private String getJsonParams(Method method, Object obj, Object[] args){
            Annotation[][] ann = method.getParameterAnnotations();
            String res = "";
            END:for (int i = 0; i < ann.length; i++) {
                Annotation[] ans = ann[i];
                for (Annotation a : ans) {
                    if (a.annotationType().getName().equals(org.springframework.web.bind.annotation.RequestBody.class.getName())){
                        Object lsarg = args[i];
                        /*Class<?> parameterType = method.getParameterTypes()[i];
                        Arrays.stream(ReflectionUtils.getAllDeclaredMethods(parameterType)).forEach(e->{
                            if (e.getName().indexOf("get")==0 && e.getModifiers()== Modifier.PUBLIC)
                            {
                                Object o = ReflectionUtils.invokeMethod(e, lsarg);
                                if (o != null) {
                                    map.put(e.getName().substring(3,4).toLowerCase()+e.getName().substring(4), o);
                                }
                            }
                        });*/
                        ObjectMapper om = new ObjectMapper();
                        if (ObjectUtils.isBasicType(lsarg) || lsarg instanceof Date) {
                            log.warn("RequestBody can not be basic type or Date,RequestBody will be ignored");
                            res = "{}";
                        }else {
                            try {
                                res = om.writeValueAsString(lsarg);
                            } catch (JsonProcessingException e) {
                                log.warn("RequestBody can not be parsed to json,RequestBody will be ignored");
                                res = "{}";
                            }
                        }
                        break END;
                    }
                }
            }
            return res;
        }


        private String getUrl(Method method){
            HttpExchange ann = method.getDeclaringClass().getAnnotation(HttpExchange.class);
            String baseUrl = ann.value();
            for (Annotation an : method.getAnnotations()) {
                if (an.annotationType().getName().equals(GetExchange.class.getName())){
                    return baseUrl + ((GetExchange) an).value();
                }
                if (an.annotationType().getName().equals(PostExchange.class.getName())){
                    return baseUrl + ((PostExchange) an).value();
                }
            }
            throw new OkHttpInterfaceUrlBuildError();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RequestMetaData methodMetaData = getMethodMetaData(method, proxy, args);
            processPathParam(methodMetaData);
            Request request;
            ObjectMapper om = new ObjectMapper();
            if (methodMetaData.getMethod().equalsIgnoreCase(GET)){
                request = processGetRequest(methodMetaData);
            }else if(methodMetaData.getMethod().equalsIgnoreCase(POST)){
                request = processPostRequest(methodMetaData);
            }else {
                throw new OkHttpInterfaceRequestMethodError();
            }
            Response resp = client.newCall(request).execute();
            if (method.getReturnType().isAssignableFrom(ResponseBody.class)) {
                return resp.body();
            }
            try (resp){
                String bodyStr = resp.body().string();
                if (!resp.isSuccessful()) {
                    OkHttpResponseError err = new OkHttpResponseError();
                    err.setBodyStr(bodyStr);
                    err.setHttpCode(resp.code());
                    err.setHttpErrorMessage(resp.message());
                    throw err;
                }
                om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                return getResponseToReturnType(method, om, bodyStr);
            }
        }

        @Nullable
        private Object getResponseToReturnType(Method method, ObjectMapper om, String bodyStr) throws JsonProcessingException {
            if (method.getReturnType().getName().equals(String.class.getName()))
                return bodyStr;
            if (method.getReturnType().getName().equals(Void.class.getName()) || method.getReturnType().getName().equals(void.class.getName()))
                return null;
            if (method.getReturnType().getName().equals(Integer.class.getName()) || method.getReturnType().getName().equals(int.class.getName()))
                return Integer.parseInt(bodyStr);
            if (method.getReturnType().getName().equals(Long.class.getName()) || method.getReturnType().getName().equals(long.class.getName()))
                return Long.parseLong(bodyStr);
            if (method.getReturnType().getName().equals(Double.class.getName()) || method.getReturnType().getName().equals(double.class.getName()))
                return Double.parseDouble(bodyStr);
            if (method.getReturnType().getName().equals(Float.class.getName()) || method.getReturnType().getName().equals(float.class.getName()))
                return Float.parseFloat(bodyStr);
            if (method.getReturnType().getName().equals(Boolean.class.getName()) || method.getReturnType().getName().equals(boolean.class.getName()))
                return Boolean.parseBoolean(bodyStr);
            if (method.getReturnType().getName().equals(Short.class.getName()) || method.getReturnType().getName().equals(short.class.getName()))
                return Short.parseShort(bodyStr);
            if (method.getReturnType().getName().equals(Byte.class.getName()) || method.getReturnType().getName().equals(byte.class.getName()))
                return Byte.parseByte(bodyStr);
            if (method.getReturnType().getName().equals(Character.class.getName()) || method.getReturnType().getName().equals(char.class.getName()))
                return bodyStr.charAt(0);
            return om.readValue(bodyStr, method.getReturnType());
        }

        @NotNull
        private Request processPostRequest(RequestMetaData methodMetaData)  {
            Request request;
            Request.Builder bd = new Request.Builder();
            methodMetaData.getHeaders().forEach(bd::addHeader);
            RequestBody body = null;
            if (methodMetaData.getJsonParams().length()>0)
            {
                body = RequestBody.create(methodMetaData.getJsonParams(), MediaType.parse("application/json"));
            }else {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                methodMetaData.getParams().forEach((k, v)->{
                    formBodyBuilder.add(k, safeGetObjectString(v));
                });
                body = formBodyBuilder.build();
            }
            bd.url(methodMetaData.getUrl());
            request = bd.post(body).build();
            return request;
        }

        @NotNull
        private Request processGetRequest(RequestMetaData methodMetaData) {
            Request request;
            Request.Builder bd = new Request.Builder().get();
            methodMetaData.getHeaders().forEach(bd::addHeader);
            StringBuilder sb = new StringBuilder(methodMetaData.getUrl() + "?");
            methodMetaData.getParams().forEach((k, v)->{
                sb.append(k).append("=").append(v).append("&");
            });
            bd.url(sb.toString());
            request = bd.build();
            return request;
        }

        private void processPathParam(RequestMetaData methodMetaData) {
            if (methodMetaData.getPathParams().size()>0)
            {
                methodMetaData.getPathParams().forEach((k, v)->{
                    methodMetaData.setUrl(methodMetaData.getUrl().replace("{"+k+"}",v));
                });
            }
        }
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(this.getClass().getClassLoader()
                , new Class[]{mapperInterface}, new RequestJdkProxy(http));
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
