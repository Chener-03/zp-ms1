package xyz.chener.zp.common.config.antiShaking.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import xyz.chener.zp.common.config.antiShaking.annotation.AntiShaking;
import xyz.chener.zp.common.config.antiShaking.error.AntiShakingError;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.RequestUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/10:47
 * @Email: chen@chener.xyz
 */

@Aspect
@Slf4j
public class AntiShakingAop {

    @Pointcut("@annotation(xyz.chener.zp.common.config.antiShaking.annotation.AntiShaking)"+
            " && (@annotation(org.springframework.web.bind.annotation.GetMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping))")
    public void asPointcut() {}

    @Around("asPointcut()")
    public Object asAround(ProceedingJoinPoint pjp) throws Throwable {
        if (pjp.getSignature() instanceof MethodSignature methodSignature) {
            AntiShaking antiShak = methodSignature.getMethod().getAnnotation(AntiShaking.class);
            AtomicReference<String> url = new AtomicReference<>(null);
            Optional.ofNullable(RequestUtils.getConcurrentRequest()).ifPresent(request -> {
                url.set(request.getRequestURI());
            });
            if (url.get() == null) {
                url.set(methodSignature.getMethod().getDeclaringClass().getName()+"."+methodSignature.getMethod().getName());
            }
            String key = buildKey(url.get(),antiShak, pjp.getArgs(), methodSignature.getParameterNames());
            try {
                AssertUrils.state(antiShak.processer().getConstructor().newInstance().check(key,antiShak.limitTimeMs())
                ,new AntiShakingError());
            }catch (Exception ec) {
                if (ec instanceof AntiShakingError) {
                    throw ec;
                }else {
                    log.warn("防抖处理异常",ec);
                }
            }
        }
        return pjp.proceed();
    }

    private String buildKey(String url,AntiShaking antiShak,Object[] args,String[] paramNames){
        StringBuilder sb = new StringBuilder(url).append(":");
        if (antiShak.hasUserAuth()) {
            sb.append(SecurityContextHolder.getContext().getAuthentication().getName()).append(":");
        }

        Arrays.stream(antiShak.validParams()).forEach(e->{
            String[] split = e.split("\\.");
            if (split.length == 1) {
                Object p = getParamObjectByName(split[0], args, paramNames);
                sb.append(ObjectUtils.nullSafeToString(p)).append(":");
            }else {
                Object p = getParamObjectByName(split[0], args, paramNames);
                sb.append(getObjByPath(p,e.substring(e.indexOf(".")+1))).append(":");
            }
        });
        return sb.toString();
    }

    private Object getParamObjectByName(String paramName,Object[] args,String[] paramNames) {
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(paramName)) {
                return args[i];
            }
        }
        return null;
    }



    private String getObjByPath(Object obj,String path) {
        if (obj instanceof List){
            log.warn("解析参数暂不支持List类型");
            return null;
        }
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            if (obj instanceof Map map) {
                return ObjectUtils.nullSafeToString((map).get(paths[0]));
            }
            return getObjFieldVal(obj,paths[0]);
        }else {
            if (obj instanceof Map map) {
                return getObjByPath(map.get(paths[0]),path.substring(path.indexOf(".")+1));
            }
            return getObjByPath(getObjFieldVal(obj,paths[0]),path.substring(path.indexOf(".")+1));
        }
    }

    private String getObjFieldVal(Object obj,String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            boolean access = f.canAccess(obj);
            f.setAccessible(true);
            Object o = f.get(obj);
            f.setAccessible(access);
            return ObjectUtils.nullSafeToString(o);
        } catch (Exception e) {
            return "null";
        }
    }


}
