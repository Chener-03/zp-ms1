package xyz.chener.zp.datasharing.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigDto;
import xyz.chener.zp.datasharing.service.DsRequestConfigService;
import xyz.chener.zp.datasharing.service.DsRequestProcessConfigService;

/**
 * @Author: chenzp
 * @Date: 2023/04/06/16:50
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web/requestConfig")
@Validated
public class RequestConfigController {

    private final DsRequestConfigService dsRequestConfigService;

    private final DsRequestProcessConfigService dsRequestProcessConfigService;


    public RequestConfigController(DsRequestConfigService dsRequestConfigService, DsRequestProcessConfigService dsRequestProcessConfigService) {
        this.dsRequestConfigService = dsRequestConfigService;
        this.dsRequestProcessConfigService = dsRequestProcessConfigService;
    }


    @GetMapping("/list")
    public PageInfo<DsRequestConfigDto> list(@ModelAttribute DsRequestConfigDto dto
        , @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        return dsRequestConfigService.getDsRequestConfigList(dto,page,size);
    }


}
