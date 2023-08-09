package xyz.chener.zp.zpusermodule.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryResult;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.dto.Auth2FaMessageDto;
import xyz.chener.zp.zpusermodule.service.User2faService;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping({"/api/web","/api/client"})
public class User2FaController {

    private final User2faService user2faService;

    public User2FaController(User2faService user2faService) {
        this.user2faService = user2faService;
    }


    @PostMapping("/2fa/enable")
    @EncryResult
    public Auth2FaMessageDto enable2Fa(){
        return user2faService.enable2Fa(SecurityContextHolder.getContext().getAuthentication().getName());
    }


}
