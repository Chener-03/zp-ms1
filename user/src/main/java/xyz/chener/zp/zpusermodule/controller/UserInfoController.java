package xyz.chener.zp.zpusermodule.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.common.config.dynamicVerification.annotation.Ds;
import xyz.chener.zp.common.config.dynamicVerification.annotation.DsEntityField;
import xyz.chener.zp.common.config.dynamicVerification.annotation.DsTargetField;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.UserExtend;
import xyz.chener.zp.zpusermodule.entity.dto.ResetPasswordDto;
import xyz.chener.zp.zpusermodule.entity.dto.UserOtherInfo;
import xyz.chener.zp.zpusermodule.error.userinfo.OnlyUpdateSelfError;
import xyz.chener.zp.zpusermodule.service.UserBaseService;
import xyz.chener.zp.zpusermodule.service.UserExtendService;

/**
 * @Author: chenzp
 * @Date: 2023/03/01/14:34
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class UserInfoController {

    private final UserExtendService userExtendService;
    private final UserBaseService userBaseService;

    public UserInfoController(UserExtendService userExtendService, UserBaseService userBaseService) {
        this.userExtendService = userExtendService;
        this.userBaseService = userBaseService;
    }

    @PostMapping("/saveSelfExtendInfo")
    @PreAuthorize("hasAnyRole('user_self_info_update')")
    public UserExtend saveUserExtendInfo(@ModelAttribute UserExtend userExtend)
    {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserBase user = userBaseService.lambdaQuery().select(UserBase::getId).eq(UserBase::getUsername, name).one();
        if (userExtend.getUserId() == null){
            userExtend.setUserId(user.getId());
        }
        if (ObjectUtils.nullSafeEquals(userExtend.getUserId(), user.getId())){
            return userExtendService.addOrUpdateUserExtend(userExtend) ;
        }
        throw new OnlyUpdateSelfError();
    }


    @PostMapping("/resetSelfPassword")
    @PreAuthorize("hasAnyRole('user_self_info_update')")
    @Ds("ds")
    public ResetPasswordDto resetPassword(@RequestParam @DsTargetField String oldPassword, String ds
            , @RequestParam @Length(min = 6,max = 20,message = "密码长度为6-20") @DsTargetField  String newPassword)
    {
        return userBaseService.resetPassword(SecurityContextHolder.getContext().getAuthentication().getName()
        ,newPassword,oldPassword);
    }


    @GetMapping("/getSelfOtherInfo")
    @PreAuthorize("hasAnyRole('user_self_info_query')")
    public UserOtherInfo getSelfOtherInfo()
    {
         return userExtendService.getSelfOtherInfo(SecurityContextHolder.getContext().getAuthentication().getName());
    }


}
