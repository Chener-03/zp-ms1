package xyz.chener.zp.zpusermodule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.QrCodeLoginRespDto;
import xyz.chener.zp.zpusermodule.service.QrCodeLoginService;

/**
 * @Author: chenzp
 * @Date: 2023/06/15/15:30
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/client")
@Validated
public class QrCodeLoginController {

    private final QrCodeLoginService qrCodeLoginService;

    public QrCodeLoginController(QrCodeLoginService qrCodeLoginService) {
        this.qrCodeLoginService = qrCodeLoginService;
    }


    @GetMapping("/user/qrCodeGet")
    public QrCodeLoginRespDto qrCodeGet(@RequestParam("uuid") String uuid){
        return qrCodeLoginService.qrCodeGet(uuid);
    }

    @PostMapping("/user/postQrCodeLoginGet")
    @PreAuthorize("hasAnyRole('microservice_call')")
    public Boolean postQrCodeLoginGet(@RequestParam("sessionId") String sessionId){
        return qrCodeLoginService.postQrCodeLoginGet(sessionId);
    }

    @RequestMapping("/user/qrCodeAuthorization")
    public Boolean qrCodeAuthorization(@RequestParam("uuid") String uuid){
        return qrCodeLoginService.qrCodeAuthorization(uuid, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @RequestMapping("/user/postQrCodeLoginAuthorization")
    @PreAuthorize("hasAnyRole('microservice_call')")
    public Boolean postQrCodeLoginAuthorization(@RequestParam("sessionId") String sessionId,@ModelAttribute LoginResult result){
        return qrCodeLoginService.postQrCodeLoginAuthorization(sessionId,result);
    }


}
