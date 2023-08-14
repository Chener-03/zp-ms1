package xyz.chener.zp.common.config.auth2fa;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.auth2fa.annotation.Auth2FA;
import xyz.chener.zp.common.config.nacosMetadataReg.MetatadaRegInterface;
import xyz.chener.zp.common.config.nacosMetadataReg.NacosMetadataRegister;
import xyz.chener.zp.common.config.requesturliterator.RequestUrlBeanDefinitionIterator;
import xyz.chener.zp.common.config.requesturliterator.UrlClassNotice;
import xyz.chener.zp.common.entity.Auth2FaRegisterMetadata;

import java.lang.reflect.Method;
import java.util.*;


@Slf4j
public class Auth2FaRegister implements UrlClassNotice , MetatadaRegInterface {

    private Auth2FaRegister(){}

    @Getter
    private static Auth2FaRegister instance = new Auth2FaRegister();

    private final String KEY = "2FA_URL_LIST";


    public static final List<Auth2FaRegisterMetadata> auth2FAUrlList = new ArrayList<>();

    @Override
    public void notice(List<? extends Class<?>> urlClass,String contextPath) {
        final String lsContextPath = Optional.ofNullable(contextPath).orElse("");
        ArrayList<Auth2FaRegisterMetadata> metadata = new ArrayList<>();

        urlClass.forEach(e->{
            List<String> classRequestMappingValues = getClassRequestMappingValues(e);
            Auth2FA class2faAnn = e.getAnnotation(Auth2FA.class);
            Method[] methods = e.getMethods();

            Arrays.stream(methods)
                    .filter(this::hasMapping)
                    .forEach(method->{
                        Auth2FA method2faAnn = method.getAnnotation(Auth2FA.class);
                        if (method2faAnn == null && class2faAnn == null){
                            return;
                        }
                        List<String> methodUrls = getMethodUrls(method);
                        methodUrls.forEach(url->{
                            if (classRequestMappingValues.isEmpty()){
                                Auth2FaRegisterMetadata mtd = new Auth2FaRegisterMetadata();
                                mtd.setRequire(method2faAnn == null?class2faAnn.require():method2faAnn.require());
                                mtd.setUrl(lsContextPath + url);
                                metadata.add(mtd);
                            }else {
                                classRequestMappingValues.forEach(classUrl->{
                                    Auth2FaRegisterMetadata mtd = new Auth2FaRegisterMetadata();
                                    mtd.setRequire(method2faAnn == null?class2faAnn.require():method2faAnn.require());
                                    mtd.setUrl(lsContextPath + classUrl + url);
                                    metadata.add(mtd);
                                });
                            }
                        });
                    });
        });

        auth2FAUrlList.addAll(metadata);
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
        try {
            map.put(KEY,new ObjectMapper().writeValueAsString(auth2FAUrlList));
        } catch (Exception e) {
            log.error("2FA_URL_LIST 注册元数据失败：",e);
        }
    }
}
