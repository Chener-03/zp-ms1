package xyz.chener.zp.zpusermodule.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.zpusermodule.config.oplog.service.OperateRecordService;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OperateRecordDto;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/14:17
 * @Email: chen@chener.xyz
 */
@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class OpRecordController {
    private final OperateRecordService operateRecordService;

    public OpRecordController(OperateRecordService operateRecordService) {
        this.operateRecordService = operateRecordService;
    }


    @GetMapping("/getOpRecordList")
    public PageInfo<OperateRecordDto> getOpRecordList(@ModelAttribute OperateRecordDto dto
            , @RequestParam(defaultValue = "1") Integer page
            , @RequestParam(defaultValue = "10") Integer size)
    {
        return operateRecordService.getList(dto,page,size);
    }

}
