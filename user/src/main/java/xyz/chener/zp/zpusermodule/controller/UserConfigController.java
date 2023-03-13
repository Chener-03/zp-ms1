package xyz.chener.zp.zpusermodule.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.opLog.annotation.OpLog;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.UserConfig;
import xyz.chener.zp.zpusermodule.service.UserConfigService;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class UserConfigController {

    private final UserConfigService userConfigService;

    public UserConfigController(UserConfigService userConfigService) {
        this.userConfigService = userConfigService;
    }

    @GetMapping("/getConcurrentUserConfig")
    public UserConfig getConcurrentUserConfig(@ModelAttribute FieldQuery fieldQuery){
        return userConfigService.getUserConfig(SecurityContextHolder.getContext().getAuthentication().getName()
                ,fieldQuery);
    }

    @PostMapping("/updateConcurrentUserLayoutConfig")
    @OpLog(operateName = "更新用户布局配置")
    public Boolean updateConcurrentUserLayoutConfig(@RequestParam("layoutConfig") String layoutConfig){
        return userConfigService.updateConcurrentUserLayoutConfig(SecurityContextHolder.getContext().getAuthentication().getName()
                ,layoutConfig);
    }

}
