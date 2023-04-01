package xyz.chener.zp.datasharing.controller;


import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebArgumentResolver;
import xyz.chener.zp.common.config.opLog.annotation.OpLog;
import xyz.chener.zp.common.config.paramDecryption.annotation.ModelAttributeDecry;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestBodyDecry;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.datasharing.config.oplog.OpRecordMybatisWrapper;
import xyz.chener.zp.datasharing.entity.DsDatasource;
import xyz.chener.zp.datasharing.entity.dto.ConnectResult;
import xyz.chener.zp.datasharing.service.DsDatasourceService;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web/datasource")
@Validated
public class DataSourceController {

    private final DsDatasourceService dsDatasourceService;

    public DataSourceController(DsDatasourceService dsDatasourceService) {
        this.dsDatasourceService = dsDatasourceService;
    }

    @PostMapping("/testConnection")
    public ConnectResult testConnection(@ModelAttributeDecry @Validated DsDatasource dsDatasource){
        return dsDatasourceService.testConnection(dsDatasource);
    }


    @PostMapping("/save")
    @OpLog(operateName = "保存数据源",recordClass = OpRecordMybatisWrapper.class)
    @Validated
    public DsDatasource save(@NotNull(message = "nnnnull") @RequestParamDecry (value = "dsDatasource",required = false) String  dsDatasource){

        return  null;
    }

}
