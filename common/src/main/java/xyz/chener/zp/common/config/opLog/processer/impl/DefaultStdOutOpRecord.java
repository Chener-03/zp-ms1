package xyz.chener.zp.common.config.opLog.processer.impl;

import xyz.chener.zp.common.config.opLog.processer.OpRecordInterface;

public class DefaultStdOutOpRecord implements OpRecordInterface {
    @Override
    public void record(String opName, String paramJson, String resultJson
            , Boolean isThrowException, Throwable throwable) {
        System.out.println(String.format("opName:%s,paramJson:%s,resultJson:%s,isThrowException:%s,throwable:%s"
                ,opName,paramJson,resultJson,isThrowException,throwable));
    }
}
