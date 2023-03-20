package xyz.chener.zp.sentinelAdapter.aop;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import xyz.chener.zp.sentinelAdapter.currentlimit.CurrentLimitManager;
import xyz.chener.zp.sentinelAdapter.currentlimit.annotation.LimitResource;
import xyz.chener.zp.sentinelAdapter.currentlimit.error.RequestTooManyError;

import java.lang.reflect.Method;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/17:04
 * @Email: chen@chener.xyz
 */


@Aspect
public class CurrentLimitAop {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(CurrentLimitAop.class);

    @Pointcut("@annotation(xyz.chener.zp.sentinelAdapter.currentlimit.annotation.LimitResource)")
    public void pointcut() { }



    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable{
        if (pjp.getSignature() instanceof MethodSignature methodSignature) {
            LimitResource limitResource = methodSignature.getMethod().getAnnotation(LimitResource.class);
            String resourceName = limitResource.value();
            resourceName = CurrentLimitManager.getRulesName(resourceName, getMethodSignature(methodSignature));
            if (resourceName == null) {
                logger.warn("获取资源名失败,限流器将无效");
                return pjp.proceed();
            }
            Entry entry = null;
            try {
                ContextUtil.enter(resourceName);
                entry = SphU.entry(resourceName);
                return pjp.proceed();
            }catch (Throwable throwable){
                if (BlockException.isBlockException(throwable)) {
                    throw new RequestTooManyError();
                }else {
                    throw throwable;
                }
            }finally {
                if (entry != null) {
                    entry.exit();
                }
                ContextUtil.exit();
            }
        }else {
            return pjp.proceed();
        }

    }

    private String getMethodSignature(MethodSignature signature) {
        Method method = signature.getMethod();
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

}
