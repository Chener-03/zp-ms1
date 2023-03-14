package xyz.chener.zp.zpusermodule.config.oplog;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.config.opLog.processer.OpRecordInterface;
import xyz.chener.zp.zpusermodule.config.oplog.dao.OperateRecordDao;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OperateRecord;

import java.util.Date;


@Slf4j
public class OpRecordMybatisWrapper implements OpRecordInterface {


    @Override
    public void record(String opName, String paramJson, String resultJson, Boolean isThrowException, Throwable throwable) {
        OperateRecordDao recordDao = null;

        try {
            recordDao = ApplicationContextHolder.getApplicationContext().getBean(OperateRecordDao.class);
        }catch (Exception exception){
            log.warn("获取数据库接口失败，无法记录操作日志");
            return;
        }

        String traceId = TraceContext.traceId();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        OperateRecord op = new OperateRecord();
        op.setTraceId(traceId);
        op.setIssuccess(!isThrowException);
        op.setOpName(opName);
        op.setParamsData(paramJson);
        op.setReturnData(resultJson);
        op.setOpTime(new Date());
        op.setUsername(username);
        if (isThrowException && throwable != null) {
            op.setFailReason(throwable.getMessage());
        }
        recordDao.insertRecord(op);

    }
}
