package xyz.chener.zp.datasharing.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import xyz.chener.zp.common.utils.ThreadUtils;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.common.utils.chain.ChainStarter;
import xyz.chener.zp.datasharing.entity.DsRequestConfig;
import xyz.chener.zp.datasharing.entity.DsRequestProcessConfig;
import xyz.chener.zp.datasharing.requestProcess.entity.RequestProcessType;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.*;
import xyz.chener.zp.datasharing.requestProcess.error.MethodNotAllowProcess;
import xyz.chener.zp.datasharing.requestProcess.error.NoSuchRequestUidErrorProcess;
import xyz.chener.zp.datasharing.requestProcess.error.RequestErrorProcess;
import xyz.chener.zp.datasharing.requestProcess.error.RequestProcessNotFoundProcess;
import xyz.chener.zp.datasharing.requestProcess.exec.*;
import xyz.chener.zp.datasharing.service.DataSharingService;
import xyz.chener.zp.datasharing.service.DsRequestConfigService;
import xyz.chener.zp.datasharing.service.DsRequestProcessConfigService;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;


@Service
public class DataSharingServiceImpl implements DataSharingService {

    private final DsRequestConfigService dsRequestConfigService;
    private final DsRequestProcessConfigService dsRequestProcessConfigService;

    public DataSharingServiceImpl(DsRequestConfigService dsRequestConfigService, DsRequestProcessConfigService dsRequestProcessConfigService) {
        this.dsRequestConfigService = dsRequestConfigService;
        this.dsRequestProcessConfigService = dsRequestProcessConfigService;
    }


    @Override
    public void sharing(String requestId, HttpServletRequest request, HttpServletResponse response) {
        DsRequestConfig requestConfig = dsRequestConfigService.lambdaQuery()
                .eq(DsRequestConfig::getRequestUid, requestId)
                .one();
        if (requestConfig == null) {
            NoSuchRequestUidErrorProcess.process(request, response);
            return;
        }

        if (!requestConfig.getEnable()){
            RequestErrorProcess.process(request, response,"当前请求已被禁用,请联系提供者.");
            return;
        }

        if (!request.getMethod().equalsIgnoreCase(requestConfig.getRequestMethod())) {
            MethodNotAllowProcess.process(request, response);
            return;
        }

        List<DsRequestProcessConfig> processList = dsRequestProcessConfigService.lambdaQuery()
                .eq(DsRequestProcessConfig::getRequestConfigId, requestConfig.getId())
                .list();
        if (processList == null || processList.size() == 0 || !RequestProcessType.checkMust(processList)) {
            RequestProcessNotFoundProcess.process(request, response);
            return;
        }

        PeAllParams allParams = new PeAllParams();

        allParams.setRequest(request);
        allParams.setResponse(response);
        allParams.setDsRequestConfig(requestConfig);
        allParams.setProcessList(processList);
        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        processList.forEach(e->{
            if (e.getType().equalsIgnoreCase(RequestProcessType.AUTH)){
                ThreadUtils.runIgnoreException(()->{
                    allParams.setAuthPe(om.readValue(e.getConfigJson(), AuthPe.class));
                });
            }

            if (e.getType().equalsIgnoreCase(RequestProcessType.IN)){
                ThreadUtils.runIgnoreException(()->{
                    allParams.setInPe(om.readValue(e.getConfigJson(), InPe.class));
                });
            }

            if (e.getType().equalsIgnoreCase(RequestProcessType.IN_JS)){
                ThreadUtils.runIgnoreException(()->{
                    allParams.setInJsPe(om.readValue(e.getConfigJson(), InJsPe.class));
                });
            }

            if (e.getType().equalsIgnoreCase(RequestProcessType.SQL)){
                ThreadUtils.runIgnoreException(()->{
                    allParams.setSqlPe(om.readValue(e.getConfigJson(), SqlPe.class));
                });
            }

            if (e.getType().equalsIgnoreCase(RequestProcessType.OUT)){
                ThreadUtils.runIgnoreException(()->{
                    allParams.setOutPe(om.readValue(e.getConfigJson(), OutPe.class));
                });
            }

            if (e.getType().equalsIgnoreCase(RequestProcessType.OUT_JS)){
                ThreadUtils.runIgnoreException(()->{
                    allParams.setOutJsPe(om.readValue(e.getConfigJson(), OutJsPe.class));
                });
            }

            if (e.getType().equalsIgnoreCase(RequestProcessType.OUT_DATA)){
                ThreadUtils.runIgnoreException(()->{
                    allParams.setOutDataPe(om.readValue(e.getConfigJson(), OutDataPe.class));
                });
            }

        });

        List<AbstractChainExecute> pl = new ArrayList<>();
        pl.add(new ParamGetterEc());
        pl.add(new AuthExec());
        pl.add(new InExec());
        pl.add(new InJsExec());
        pl.add(new SqlExec());
        pl.add(new OutExec());
        pl.add(new OutJsExec());
        pl.add(new OutDataExec());

        try {
            Object start = ChainStarter.start(pl, allParams);
            System.out.println();
        } catch (Exception e) {
            RequestErrorProcess.process(request, response,e.getMessage());
        }

    }
}
