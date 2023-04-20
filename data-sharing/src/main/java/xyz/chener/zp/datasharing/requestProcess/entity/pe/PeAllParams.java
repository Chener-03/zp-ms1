package xyz.chener.zp.datasharing.requestProcess.entity.pe;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import xyz.chener.zp.datasharing.entity.DsRequestConfig;
import xyz.chener.zp.datasharing.entity.DsRequestProcessConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class PeAllParams {

    private AuthPe authPe;

    private InPe inPe;

    private InJsPe inJsPe;

    private SqlPe sqlPe;

    private OutPe outPe;

    private OutJsPe outJsPe;

    private OutDataPe outDataPe;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private DsRequestConfig dsRequestConfig;

    private List<DsRequestProcessConfig> processList;

    private Map<String,String> normalParams = new TreeMap<>();

    // 结果集  res0->sql0  res1->sql1  res2->sql2 .......
    private Map<String,List<Map<String,Object>>> result = new TreeMap<>();

}
