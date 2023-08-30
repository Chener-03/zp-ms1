package xyz.chener.zp.zpusermodule.controller;


import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.entity.CommonVar;
import xyz.chener.zp.common.entity.LoginUserDetails;
import xyz.chener.zp.common.entity.WriteList;
import xyz.chener.zp.zpusermodule.entity.Dictionaries;
import xyz.chener.zp.zpusermodule.entity.DictionariesKeyEnum;
import xyz.chener.zp.zpusermodule.entity.dto.ClientAppVersionDto;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.OwnInformation;
import xyz.chener.zp.zpusermodule.entity.dto.UserOtherInfo;
import xyz.chener.zp.zpusermodule.service.DictionariesService;
import xyz.chener.zp.zpusermodule.service.UserBaseService;
import xyz.chener.zp.zpusermodule.service.UserExtendService;

import java.util.Objects;
import java.util.Optional;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping(CommonVar.CLIENT_URL_PREFIX)
@Validated
@RequiredArgsConstructor
public class UserClientController {

    private final UserBaseService userBaseService;

    private final DictionariesService dictionariesService;

    private UserExtendService userExtendService;


    @Autowired
    @Lazy
    public void setUserExtendService(UserExtendService userExtendService) {
        this.userExtendService = userExtendService;
    }

    @PostMapping("/userDoLogin")
    @WriteList
    @Validated
    public LoginResult doLoginClient(@NotNull @RequestParamDecry(value = "username") String username,@NotNull @RequestParamDecry(value = "password") String password){
        return userBaseService.doLoginClient(username, password);
    }

    @GetMapping("/getAndroidAppVersion")
    @WriteList
    public ClientAppVersionDto getAndroidAppVersion(){
        Dictionaries dictionaries = dictionariesService.lambdaQuery().eq(Dictionaries::getId, DictionariesKeyEnum.ANDROID_APP_VERSION).one();
        if (dictionaries != null){
            return new ClientAppVersionDto(dictionaries.getValue0(),Boolean.parseBoolean(Optional.ofNullable(dictionaries.getValue1()).orElse("true")));
        }
        return new ClientAppVersionDto("1",true);
    }


    @GetMapping("/getConcurrentUserInformation")
    @PreAuthorize("hasAnyRole('get_own_information')")
    public OwnInformation getConcurrentUserInformation()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && StringUtils.hasText(authentication.getName()))
            return userBaseService.getUserInformation(authentication.getName());
        return new OwnInformation();
    }

    @GetMapping("/getSelfOtherInfo")
    @PreAuthorize("hasAnyRole('user_self_info_query')")
    public UserOtherInfo getSelfOtherInfo()
    {
        return userExtendService.getSelfOtherInfo(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
