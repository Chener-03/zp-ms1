package xyz.chener.zp.common.config.okhttpclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import okhttp3.*;
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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    private class RequestJdkProxy implements InvocationHandler, Serializable{

        @Data
        private static class RequestMetaData{
            private String url;
            private String method;
            private Map<String,Object> params;
            private Map<String,Object> jsonParams;
            private Map<String,String> headers;
            private Map<String,String> pathParams;
        }

        private OkHttpClient client;


        public RequestJdkProxy(OkHttpClient client) {
            this.client = client;
        }

        private RequestMetaData getMethodMetaData(Method method,Object obj,Object[] args){
            RequestMetaData md = new RequestMetaData();
            md.setUrl(getUrl(method));
            GetExchange getAnn = method.getAnnotation(GetExchange.class);
            PostExchange postAnn = method.getAnnotation(PostExchange.class);
            AssertUrils.state(getAnn != null || postAnn != null, OkHttpInterfaceRequestMethodError.class);
            Map<String, String> headers = getHeaders(method, obj,args);
            Map<String, Object> params = getFormDataParams(method, obj, args);
            Map<String, Object> jsonParams = getJsonParams(method, obj, args);
            Map<String, String> pathParam = getPathParam(method, obj, args);
            if (getAnn != null) {
                md.setMethod("GET");
            }else {
                md.setMethod("POST");
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
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < ann.length; i++) {
                Annotation[] ans = ann[i];
                for (Annotation a : ans) {
                    if (a.annotationType().getName().equals(RequestParam.class.getName())) {
                        RequestParam rh = (RequestParam) a;
                        map.put(rh.value(), safeGetObjectString(args[i]));
                        break;
                    }
                }
            }
            return map;
        }

        private Map<String,Object> getJsonParams(Method method, Object obj, Object[] args){
            Annotation[][] ann = method.getParameterAnnotations();
            Map<String, Object> map = new HashMap<>();
            END:for (int i = 0; i < ann.length; i++) {
                Annotation[] ans = ann[i];
                for (Annotation a : ans) {
                    if (a.annotationType().getName().equals(org.springframework.web.bind.annotation.RequestBody.class.getName())){
                        Class<?> parameterType = method.getParameterTypes()[i];
                        Object lsarg = args[i];
                        Arrays.stream(ReflectionUtils.getAllDeclaredMethods(parameterType)).forEach(e->{
                            if (e.getName().indexOf("get")==0 && e.getModifiers()== Modifier.PUBLIC)
                            {
                                Object o = ReflectionUtils.invokeMethod(e, lsarg);
                                if (o != null) {
                                    map.put(e.getName().substring(3,4).toLowerCase()+e.getName().substring(4), o);
                                }
                            }
                        });
                        break END;
                    }
                }
            }
            return map;
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
            if (methodMetaData.getPathParams().size()>0)
            {
                methodMetaData.getPathParams().forEach((k,v)->{
                    methodMetaData.setUrl(methodMetaData.getUrl().replace("{"+k+"}",v));
                });
            }
            Request request;
            ObjectMapper om = new ObjectMapper();
            if (methodMetaData.getMethod().equalsIgnoreCase("GET")){
                Request.Builder bd = new Request.Builder().get();
                methodMetaData.getHeaders().forEach(bd::addHeader);
                StringBuilder sb = new StringBuilder(methodMetaData.getUrl() + "?");
                methodMetaData.getParams().forEach((k,v)->{
                    sb.append(k).append("=").append(v).append("&");
                });
                bd.url(sb.toString());
                request = bd.build();
            }else {
                Request.Builder bd = new Request.Builder();
                methodMetaData.getHeaders().forEach(bd::addHeader);
                RequestBody body = null;
                if (methodMetaData.getJsonParams().size()>0)
                {
                    body = RequestBody.create(om.writeValueAsString(methodMetaData.getJsonParams()), MediaType.parse("application/json"));
                }else {
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    methodMetaData.getParams().forEach((k,v)->{
                        if (v instanceof String){
                            formBodyBuilder.add(k, (String) v);
                        }else if (v instanceof Integer) {
                            formBodyBuilder.add(k, String.valueOf(v));
                        }else if (v instanceof Long) {
                            formBodyBuilder.add(k, String.valueOf(v));
                        }else if (v instanceof Double) {
                            formBodyBuilder.add(k, String.valueOf(v));
                        }else if (v instanceof Float) {
                            formBodyBuilder.add(k, String.valueOf(v));
                        }else if (v instanceof Boolean) {
                            formBodyBuilder.add(k, String.valueOf(v));
                        } else {
                            try {
                                formBodyBuilder.add(k, om.writeValueAsString(v));
                            } catch (JsonProcessingException e) { }
                        }
                    });
                    body = formBodyBuilder.build();
                }
                bd.url(methodMetaData.getUrl());
                request = bd.post(body).build();
            }
            Response resp = client.newCall(request).execute();
            if (method.getReturnType().isAssignableFrom(RequestBody.class)) {
                return resp.body();
            }
            try {
                String bodyStr = resp.body().string();
                if (!resp.isSuccessful()) {
                    OkHttpResponseError err = new OkHttpResponseError();
                    err.setBodyStr(bodyStr);
                    err.setHttpCode(resp.code());
                    err.setHttpErrorMessage(resp.message());
                    throw err;
                }
                om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                if (method.getReturnType().getName().equals(String.class.getName()))
                    return bodyStr;
                return om.readValue(bodyStr, method.getReturnType());
            }finally {
                resp.close();
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
