package xyz.chener.zp.system.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.entity.WriteList;
import xyz.chener.zp.system.entity.InstanceBaseHealth;
import xyz.chener.zp.system.entity.dto.InstanceDto;
import xyz.chener.zp.system.service.SystemInfoSerivce;
import xyz.chener.zp.system.service.impl.SystemInfoSerivceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/16:53
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class SystemInfoController {


    private final SystemInfoSerivce systemInfoSerivce;

    public SystemInfoController(SystemInfoSerivce systemInfoSerivce) {
        this.systemInfoSerivce = systemInfoSerivce;
    }


    @GetMapping("/getInstances")
    public List<InstanceDto> getInstances(@ModelAttribute InstanceDto instanceDto)
    {
        return systemInfoSerivce.getInstances(instanceDto);
    }

    @GetMapping("/getInstanceInfo")
    public List<InstanceBaseHealth> getInstanceInfo(@RequestParamDecry String url){
        return systemInfoSerivce.getInstanceInfo(url);
    }


    @GetMapping("/getSentinelInfo")
    public Map getSentinelInfo(@RequestParamDecry String url,@RequestParam String resourceName) {
        return systemInfoSerivce.getSentinelInfo(url, resourceName);
    }

}
