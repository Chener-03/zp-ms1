package xyz.chener.zp.system.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.entity.WriteList;
import xyz.chener.zp.system.entity.dto.InstanceDto;
import xyz.chener.zp.system.service.SystemInfoSerivce;
import xyz.chener.zp.system.service.impl.SystemInfoSerivceImpl;

import java.util.List;

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

    @WriteList
    @RequestMapping("/test")
    public void test()
    {
        ApplicationContextHolder.getApplicationContext()
                .getBean(SystemInfoSerivceImpl.class).getInstanceInfo("","");
    }


}
