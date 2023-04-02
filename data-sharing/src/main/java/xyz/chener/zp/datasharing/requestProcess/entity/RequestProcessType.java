package xyz.chener.zp.datasharing.requestProcess.entity;

import xyz.chener.zp.datasharing.entity.DsRequestProcessConfig;

import java.util.List;
import java.util.function.Predicate;

public class RequestProcessType {

    // 用于处理器的类型  顺序为 auth -> *in -> injs -> *sql ->  *out -> outjs -> *outdata

    public static final String AUTH = "auth";
    public static final String IN = "in";

    public static final String OUT = "out";

    public static final String SQL = "sql";

    public static final String IN_JS = "injs";

    public static final String OUT_JS = "outjs";
    public static final String OUT_DATA = "outdata";


    public static boolean checkMust( List<DsRequestProcessConfig> processList){
        List<String> list = processList.stream().map(DsRequestProcessConfig::getType).toList();
        return list.contains(IN) && list.contains(SQL) && list.contains(OUT) && list.contains(OUT_DATA);
    }

}
