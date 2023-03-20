package xyz.chener.zp.sentinelAdapter.aop;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import xyz.chener.zp.sentinelAdapter.sphu.annotation.CircuitBreakResource;
import xyz.chener.zp.sentinelAdapter.sphu.error.CircuitBreakError;

import java.lang.reflect.Method;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/17:04
 * @Email: chen@chener.xyz
 */


@Aspect
public class SphuAop  {


    @Pointcut("@annotation(xyz.chener.zp.sentinelAdapter.sphu.annotation.CircuitBreakResource)")
    public void dsPointcut() { }



    @Around("dsPointcut()")
    public Object dsAround(ProceedingJoinPoint pjp) throws Throwable{
        if (pjp.getSignature() instanceof MethodSignature methodSignature) {
            CircuitBreakResource circuitBreakResource = methodSignature.getMethod().getAnnotation(CircuitBreakResource.class);
            String resourceName = circuitBreakResource.value();
            Entry entry = null;
            try {
                ContextUtil.enter(resourceName);
                entry = SphU.entry(resourceName);
            }catch (Throwable throwable){
                if (StringUtils.hasText(circuitBreakResource.failCallBackBeanName())) {
                    try {
                        Method failMethod = methodSignature.getMethod().getDeclaringClass().getMethod(circuitBreakResource.failCallBackBeanName());
                        return failMethod.invoke(pjp.getTarget(),pjp.getArgs());
                    }catch (Exception e){
                        throw new CircuitBreakError();
                    }
                }else {
                    throw new CircuitBreakError();
                }
            }finally {
                if (entry != null) {
                    entry.exit();
                }
                ContextUtil.exit();
            }
        }
        return pjp.proceed();
    }

}
