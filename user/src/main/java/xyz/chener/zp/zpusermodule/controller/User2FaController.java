package xyz.chener.zp.zpusermodule.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.auth2fa.annotation.Auth2FA;
import xyz.chener.zp.common.config.dynamicVerification.annotation.Ds;
import xyz.chener.zp.common.config.dynamicVerification.annotation.DsTargetField;
import xyz.chener.zp.common.config.paramDecryption.annotation.ModelAttributeDecry;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryResult;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.User2fa;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.Auth2FaMessageDto;
import xyz.chener.zp.zpusermodule.service.User2faService;
import xyz.chener.zp.zpusermodule.service.UserBaseService;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping({"/api/web","/api/client"})
@RequiredArgsConstructor
public class User2FaController {

    private final User2faService user2faService;

    private final UserBaseService userBaseService;



    @PostMapping("/2fa/enable")
    @EncryResult
    public Auth2FaMessageDto enable2Fa(){
        return user2faService.enable2Fa(SecurityContextHolder.getContext().getAuthentication().getName());
    }


    @PostMapping("/2fa/confirmEnable")
    @EncryResult
    @Ds(value = "ds")
    public Auth2FaMessageDto confirmEnable2Fa(@ModelAttributeDecry @DsTargetField Auth2FaMessageDto data,@RequestParam("ds") String ds){
        return user2faService.confirmEnable2Fa(data,SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/2fa/verify")
    @PreAuthorize("hasAnyRole('microservice_call')")
    public Boolean verify2Fa(@RequestParam(value = "code",defaultValue = "") String code
            ,@RequestParam("username") String username){
        return user2faService.verify2Fa(code,username);
    }

    @GetMapping("/2fa/checkStatus")
    public Boolean check2FaStatus(){
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return false;
        }
        UserBase user = userBaseService.lambdaQuery().eq(UserBase::getUsername, SecurityContextHolder.getContext().getAuthentication().getName()).one();
        if (user == null) {
            return false;
        }
        return user2faService.lambdaQuery().eq(User2fa::getUserId, user.getId()).count() > 0;
    }

    @PostMapping("/2fa/disable")
    @Auth2FA
    public Boolean disable2Fa(){
        return user2faService.disable2Fa(null,SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
