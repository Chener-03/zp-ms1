package xyz.chener.zp.common.config.auth2fa;


import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.auth2fa.annotation.Auth2FA;
import xyz.chener.zp.common.config.nacosMetadataReg.MetatadaRegInterface;
import xyz.chener.zp.common.config.nacosMetadataReg.NacosMetadataRegister;
import xyz.chener.zp.common.config.requesturliterator.RequestUrlBeanDefinitionIterator;
import xyz.chener.zp.common.config.requesturliterator.UrlClassNotice;

import java.lang.reflect.Method;
import java.util.*;



public class Auth2FaRegister implements UrlClassNotice , MetatadaRegInterface {

    private Auth2FaRegister(){}

    @Getter
    private static Auth2FaRegister instance = new Auth2FaRegister();

    private final String KEY = "2FA_URL_LIST";

    private final String DIVISION = "####";

    public static final List<String> auth2FAUrlList = new ArrayList<>();

    @Override
    public void notice(List<? extends Class<?>> urlClass,String contextPath) {
        final String lsContextPath = Optional.ofNullable(contextPath).orElse("");
        ArrayList<String> urls = new ArrayList<>();

        urlClass.forEach(e->{
            List<String> classRequestMappingValues = getClassRequestMappingValues(e);
            boolean containsAll = e.getAnnotation(Auth2FA.class) != null;
            Method[] methods = e.getMethods();

            Arrays.stream(methods)
                    .filter(this::hasMapping)
                    .forEach(method->{
                        if (method.getAnnotation(Auth2FA.class) == null && !containsAll){
                            return;
                        }
                        List<String> methodUrls = getMethodUrls(method);
                        methodUrls.forEach(url->{
                            if (classRequestMappingValues.isEmpty()){
                                urls.add(lsContextPath + url);
                            }else {
                                classRequestMappingValues.forEach(classUrl->{
                                    urls.add(lsContextPath + classUrl + url);
                                });
                            }
                        });
                    });
        });

        auth2FAUrlList.addAll(urls);
    }

    private List<String> getClassRequestMappingValues(Class<?> clazz){
        RequestMapping requestMappingAnn = clazz.getAnnotation(RequestMapping.class);
        if (requestMappingAnn != null){
            return List.of(requestMappingAnn.value());
        }
        return Collections.EMPTY_LIST;
    }

    private Boolean hasMapping(Method method){
        return method.getAnnotation(RequestMapping.class) != null
                || method.getAnnotation(GetMapping.class) != null
                || method.getAnnotation(PostMapping.class) != null
                || method.getAnnotation(PutMapping.class) != null
                || method.getAnnotation(DeleteMapping.class) != null
                || method.getAnnotation(PatchMapping.class) != null;
    }

    private List<String> getMethodUrls(Method method){
        ArrayList<String> methodUrls = new ArrayList<>();
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            methodUrls.addAll(Arrays.asList(requestMapping.value()));
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            methodUrls.addAll(Arrays.asList(getMapping.value()));
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            methodUrls.addAll(Arrays.asList(postMapping.value()));
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            methodUrls.addAll(Arrays.asList(putMapping.value()));
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            methodUrls.addAll(Arrays.asList(deleteMapping.value()));
        }
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            methodUrls.addAll(Arrays.asList(patchMapping.value()));
        }
        return methodUrls.stream().filter(e-> e != null && !e.isEmpty()).toList();
    }


    @Override
    public void registerMetadata(Map<String, String> map) {
        map.put(KEY,String.join(DIVISION,auth2FAUrlList));
    }
}
