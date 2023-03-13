package xyz.chener.zp.zpusermodule.config.oplog;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.security.core.context.SecurityContextHolder;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.config.opLog.processer.OpRecordInterface;
import xyz.chener.zp.zpusermodule.config.oplog.dao.OperateRecordDao;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OperateRecord;

import java.util.Date;

public class OpRecordMybatisWrapper implements OpRecordInterface {


    @Override
    public void record(String opName, String paramJson, String resultJson, Boolean isThrowException, Throwable throwable) {
        OperateRecordDao recordDao = ApplicationContextHolder.getApplicationContext().getBean(OperateRecordDao.class);
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
