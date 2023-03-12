package xyz.chener.zp.common.config.opLog.processer;

public interface OpRecordInterface {

    void record(String opName,String paramJson,String resultJson
            ,Boolean isThrowException,Throwable throwable);

}
