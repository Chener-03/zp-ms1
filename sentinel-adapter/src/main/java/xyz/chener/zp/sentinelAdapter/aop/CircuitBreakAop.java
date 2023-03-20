package xyz.chener.zp.sentinelAdapter.aop;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;
import xyz.chener.zp.sentinelAdapter.circuitbreak.CircuitBreakRuleManager;
import xyz.chener.zp.sentinelAdapter.circuitbreak.annotation.CircuitBreakResource;
import xyz.chener.zp.sentinelAdapter.circuitbreak.error.CircuitBreakError;

import java.lang.reflect.Method;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/17:04
 * @Email: chen@chener.xyz
 */


@Aspect
public class CircuitBreakAop {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(CircuitBreakAop.class);

    @Pointcut("@annotation(xyz.chener.zp.sentinelAdapter.circuitbreak.annotation.CircuitBreakResource)")
    public void pointcut() { }



    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable{
        if (pjp.getSignature() instanceof MethodSignature methodSignature) {
            CircuitBreakResource circuitBreakResource = methodSignature.getMethod().getAnnotation(CircuitBreakResource.class);
            String resourceName = circuitBreakResource.value();
            resourceName = CircuitBreakRuleManager.getRulesName(resourceName, getMethodSignature(methodSignature));
            if (resourceName == null) {
                logger.warn("获取资源命失败,熔断器将无效");
                return pjp.proceed();
            }
            Entry entry = null;
            try {
                ContextUtil.enter(resourceName);
                entry = SphU.entry(resourceName);
                return pjp.proceed();
            }catch (Throwable throwable){
                if (!BlockException.isBlockException(throwable)) {
                    Tracer.traceEntry(throwable, entry);
                }

                if (StringUtils.hasText(circuitBreakResource.failCallBackMethodName())) {
                    try {
                        Method failMethod = methodSignature.getMethod().getDeclaringClass()
                                .getDeclaredMethod(circuitBreakResource.failCallBackMethodName()
                                        ,methodSignature.getParameterTypes());

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

    private String getMethodSignature(MethodSignature signature) {
        Method method = signature.getMethod();
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

}
