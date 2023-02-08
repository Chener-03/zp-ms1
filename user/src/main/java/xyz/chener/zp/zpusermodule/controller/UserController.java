package xyz.chener.zp.zpusermodule.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.common.entity.DictionaryKeyEnum;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.entity.WriteList;
import xyz.chener.zp.common.error.HttpParamErrorException;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.CustomFieldQuery;
import xyz.chener.zp.zpusermodule.entity.Dictionaries;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.UserExtend;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.OwnInformation;
import xyz.chener.zp.zpusermodule.entity.dto.UserAllInfoDto;
import xyz.chener.zp.zpusermodule.error.SqlRunException;
import xyz.chener.zp.zpusermodule.error.user.DisableUserIsConcurrent;
import xyz.chener.zp.zpusermodule.error.user.UserIsExitsException;
import xyz.chener.zp.zpusermodule.service.impl.DictionariesServiceImpl;
import xyz.chener.zp.zpusermodule.service.impl.UserBaseServiceImpl;
import xyz.chener.zp.zpusermodule.service.impl.UserExtendServiceImpl;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @Author: chenzp
 * @Date: 2023/01/11/15:43
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class UserController {

    private final UserBaseServiceImpl userBaseService;
    private final UserExtendServiceImpl userExtendService;
    private final DictionariesServiceImpl dictionariesService;

    public UserController(UserBaseServiceImpl userBaseService, UserExtendServiceImpl userExtendService, DictionariesServiceImpl dictionariesService) {
        this.userBaseService = userBaseService;
        this.userExtendService = userExtendService;
        this.dictionariesService = dictionariesService;
    }

    @GetMapping("/getUserBaseInfo")
    @PreAuthorize("hasAnyRole('microservice_call','user_user_list')")
    public PageInfo<UserBase> getUserBaseInfo(@ModelAttribute UserBase userBase
            ,@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer size)
    {
        try {
            PageHelper.startPage(page,size);
            return new PageInfo<>(userBaseService.lambdaQuery(userBase).list());
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new SqlRunException(R.ErrorMessage.SQL_RUN_ERROR.get());
        }
    }


    @GetMapping("/getUserAllInfo")
    @PreAuthorize("hasAnyRole('microservice_call','user_user_list')")
    public PageInfo<UserAllInfoDto> getUserAllInfo(@ModelAttribute UserAllInfoDto userAllInfo
            , @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size
            ,@RequestParam(defaultValue = "false") Boolean isLike
            ,@RequestParam(defaultValue = "false") Boolean roleNotNull,@ModelAttribute CustomFieldQuery c)
    {
        try {
            if (isLike)
            {
                userAllInfo.setUsername(StringUtils.hasText(userAllInfo.getUsername())?userAllInfo.getUsername():null);
                userAllInfo.setPhone(StringUtils.hasText(userAllInfo.getPhone())?userAllInfo.getPhone():null);
                userAllInfo.setEmail(StringUtils.hasText(userAllInfo.getEmail())?userAllInfo.getEmail():null);
                userAllInfo.setNameCn(StringUtils.hasText(userAllInfo.getNameCn())?userAllInfo.getNameCn():null);
                Optional.ofNullable(userAllInfo.getUsername()).ifPresent(s -> userAllInfo.setUsername("%" + s + "%"));
                Optional.ofNullable(userAllInfo.getPhone()).ifPresent(s -> userAllInfo.setPhone("%" + s + "%"));
                Optional.ofNullable(userAllInfo.getEmail()).ifPresent(s -> userAllInfo.setEmail("%" + s + "%"));
                Optional.ofNullable(userAllInfo.getNameCn()).ifPresent(s -> userAllInfo.setNameCn("%" + s + "%"));
            }

            return userBaseService.getUserAllInfo(userAllInfo,page,size,roleNotNull);
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            throw new SqlRunException(R.ErrorMessage.SQL_RUN_ERROR.get());
        }
    }




    @PostMapping("/userDoLogin")
    @WriteList
    public LoginResult userDoLogin(String username, String phone, String email
            , @RequestParam String password, @RequestParam String verification)
    {
        if (!StringUtils.hasText(username) &&
                !StringUtils.hasText(phone) &&
                !StringUtils.hasText(email))
        {
            throw new HttpParamErrorException();
        }
        return userBaseService.processLogin(username, phone, email, password, verification);
    }



    @GetMapping("/getConcurrentUserInformation")
    @PreAuthorize("hasAnyRole('microservice_call','get_own_information')")
    public OwnInformation getConcurrentUserInformation()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && StringUtils.hasText(authentication.getName()))
            return userBaseService.getUserInformation(authentication.getName());
        return new OwnInformation();
    }



    @PostMapping("/saveUserBaseInfo")
    @PreAuthorize("hasAnyRole('user_user_query')")
    public UserBase saveUserBaseInfo(@ModelAttribute @Validated UserBase userBase)
    {
        try {
            return userBaseService.addOrUpdateUser(userBase);
        }catch (DuplicateKeyException exception)
        {
            throw new UserIsExitsException();
        }
    }


    @PostMapping("/saveUserExtendInfo")
    @PreAuthorize("hasAnyRole('user_user_query')")
    public UserExtend saveUserExtendInfo(@ModelAttribute UserExtend userExtend)
    {
        return userExtendService.addOrUpdateUserExtend(userExtend);
    }


    @PostMapping("/setUserDisable")
    @PreAuthorize("hasAnyRole('user_user_query')")
    public Boolean setUserDisable(@RequestParam @Length(min = 3,max = 20,message = "用户名长度3-20") String username
            ,@RequestParam Integer disable)
    {
        AssertUrils.state(username!=null && disable!=null,HttpParamErrorException.class);
        AssertUrils.state(!SecurityContextHolder.getContext()
                .getAuthentication().getName().equals(username), DisableUserIsConcurrent.class);
        try {
            return userBaseService.lambdaUpdate()
                    .set(UserBase::getDisable,disable)
                    .eq(UserBase::getUsername,username).update();
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            throw new SqlRunException(R.ErrorMessage.SQL_RUN_ERROR.get());
        }
    }


    @PostMapping("/resetPassword")
    @PreAuthorize("hasAnyRole('user_user_query')")
    public Boolean resetPassword(@RequestParam @Length(min = 3,max = 20,message = "用户名长度3-20") String username
            , @RequestParam Boolean isPwd)
    {
        try {
            if (Boolean.TRUE.equals(isPwd))
            {
                Dictionaries dic = dictionariesService.lambdaQuery().eq(Dictionaries::getId, DictionaryKeyEnum.DEFAULT_PASSWORD.get()).one();
                return userBaseService.lambdaUpdate().set(UserBase::getPassword, dic.getValue0())
                        .eq(UserBase::getUsername,username).update();
            }else
            {
                return userBaseService.lambdaUpdate().set(UserBase::getDs, UUID.randomUUID().toString())
                        .eq(UserBase::getUsername,username).update();
            }
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            throw new SqlRunException(R.ErrorMessage.SQL_RUN_ERROR.get());
        }
    }

    @PostMapping("/deleteUser")
    @PreAuthorize("hasAnyRole('user_user_query')")
    public Boolean deleteUser(@RequestParam @Length(min = 3,max = 20,message = "用户名长度3-20") String username)
    {
        try {
            UserBase user = userBaseService.lambdaQuery().eq(UserBase::getUsername, username).one();
            boolean res = false;
            if (Objects.nonNull(user))
            {
                res = userBaseService.lambdaUpdate().eq(UserBase::getId,user.getId()).remove();
                userExtendService.lambdaUpdate().eq(UserExtend::getUserId,user.getId()).remove();
            }
            return res;
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            throw new SqlRunException(R.ErrorMessage.SQL_RUN_ERROR.get());
        }
    }

}
