package xyz.chener.zp.common.config.opLog.aop;


import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.opLog.annotation.OpLog;
import xyz.chener.zp.common.config.opLog.processer.OpRecordInterface;

import java.util.LinkedHashMap;

@Aspect
@Slf4j
public class OpRecordAop {


    @Pointcut("@annotation(xyz.chener.zp.common.config.opLog.annotation.OpLog)")
    public void opPointcut() {}

    @Around("opPointcut()")
    public Object opAround(ProceedingJoinPoint pjp) throws Throwable {
        String paramJson = null;
        String resultJson = null;

        ObjectMapper om = new ObjectMapper();
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        try {
            Object[] args = pjp.getArgs();
            for (int i = 0; i < args.length; i++) {
                map.put("arg" + i, args[i]);
            }
            paramJson = om.writeValueAsString(map);
        }catch (Exception ignored){ }
        try {
            Object res = pjp.proceed();
            map.clear();
            map.put("result", res);
            try {
                resultJson = om.writeValueAsString(map);
            }catch (Exception ignored){ }
            callRecord(pjp,paramJson,resultJson,false,null);
            return res;
        }catch (Throwable throwable){
            callRecord(pjp,paramJson,resultJson,true,throwable);
            throw throwable;
        }
    }

    private void callRecord(ProceedingJoinPoint pjp,String paramJson,String resultJson
            ,Boolean isThrowException,Throwable throwable){
        try {
            OpLog opLog = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(OpLog.class);
            String opName = opLog.operateName();
            if (StringUtils.hasText(opName)){
                OpRecordInterface o = (OpRecordInterface) opLog.recordClass().getConstructor().newInstance();
                o.record(opName,paramJson,resultJson,isThrowException,throwable);
            }
        }catch (Exception ignored){}
    }


}
