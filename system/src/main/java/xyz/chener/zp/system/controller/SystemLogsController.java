package xyz.chener.zp.system.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.entity.vo.PageInfo;
import xyz.chener.zp.system.entity.dto.LogEntityDto;
import xyz.chener.zp.system.service.SystemLogService;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class SystemLogsController {

    private final SystemLogService systemLogService;

    public SystemLogsController(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @GetMapping("/getAppLogs")
    public PageInfo<LogEntityDto> getAppLogs(@ModelAttribute LogEntityDto dto
            , @RequestParam(defaultValue = "1") Integer page
            , @RequestParam(defaultValue = "10") Integer size)
    {
        return systemLogService.getAppLogs(dto, page, size);
    }

}
