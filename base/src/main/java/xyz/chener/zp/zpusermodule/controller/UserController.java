package xyz.chener.zp.zpusermodule.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.bouncycastle.cert.ocsp.Req;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.async.WebAsyncTask;
import xyz.chener.zp.common.config.antiShaking.annotation.AntiShaking;
import xyz.chener.zp.common.config.auth2fa.annotation.Auth2FA;
import xyz.chener.zp.common.config.opLog.annotation.OpLog;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryResult;
import xyz.chener.zp.common.entity.CommonVar;
import xyz.chener.zp.common.entity.DictionaryKeyEnum;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.entity.WriteList;
import xyz.chener.zp.common.error.HttpParamErrorException;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.DemonstrationSystemUtils;
import xyz.chener.zp.common.utils.MapBuilder;
import xyz.chener.zp.common.utils.RequestUtils;
import xyz.chener.zp.sentinelAdapter.circuitbreak.CircuitBreakRuleManager;
import xyz.chener.zp.sentinelAdapter.circuitbreak.annotation.CircuitBreakResource;
import xyz.chener.zp.sentinelAdapter.currentlimit.annotation.LimitResource;
import xyz.chener.zp.zpusermodule.config.oplog.OpRecordMybatisWrapper;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OpEnum;
import xyz.chener.zp.zpusermodule.entity.*;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.OnlineUserInfo;
import xyz.chener.zp.zpusermodule.entity.dto.OwnInformation;
import xyz.chener.zp.zpusermodule.entity.dto.UserAllInfoDto;
import xyz.chener.zp.zpusermodule.error.SqlRunException;
import xyz.chener.zp.zpusermodule.error.role.DefaultRoleDeleteError;
import xyz.chener.zp.zpusermodule.error.user.DisableUserIsConcurrent;
import xyz.chener.zp.zpusermodule.error.user.UserIsExitsException;
import xyz.chener.zp.zpusermodule.service.UserLoginEventRecordService;
import xyz.chener.zp.zpusermodule.service.impl.DictionariesServiceImpl;
import xyz.chener.zp.zpusermodule.service.impl.UserBaseServiceImpl;
import xyz.chener.zp.zpusermodule.service.impl.UserExtendServiceImpl;
import xyz.chener.zp.zpusermodule.ws.WsCache;
import xyz.chener.zp.zpusermodule.ws.entity.WsClient;
import xyz.chener.zp.zpusermodule.ws.WsMessagePublisher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: chenzp
 * @Date: 2023/01/11/15:43
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping({"/api/web"})
@Validated
public class UserController {

    private final UserBaseServiceImpl userBaseService;
    private final UserExtendServiceImpl userExtendService;
    private final DictionariesServiceImpl dictionariesService;
    private final UserLoginEventRecordService loginEventRecordService;

    public UserController(UserBaseServiceImpl userBaseService, UserExtendServiceImpl userExtendService, DictionariesServiceImpl dictionariesService, UserLoginEventRecordService loginEventRecordService) {
        this.userBaseService = userBaseService;
        this.userExtendService = userExtendService;
        this.dictionariesService = dictionariesService;
        this.loginEventRecordService = loginEventRecordService;
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


    @EncryResult
    @GetMapping("/getUserAllInfo")
    @PreAuthorize("hasAnyRole('microservice_call','user_user_list')")
    public PageInfo<UserAllInfoDto> getUserAllInfo(@ModelAttribute UserAllInfoDto userAllInfo
            , @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size
            , @RequestParam(defaultValue = "false") Boolean isLike
            , @RequestParam(defaultValue = "false") Boolean roleNotNull )
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
    public LoginResult userDoLogin(@RequestParamDecry(value = "username") String username
            ,@RequestParamDecry(value = "phone") String phone
            ,@RequestParamDecry(value = "email") String email
            , @RequestParamDecry(value = "password") String password)
    {
        if (!StringUtils.hasText(username) &&
                !StringUtils.hasText(phone) &&
                !StringUtils.hasText(email))
        {
            throw new HttpParamErrorException();
        }
        return userBaseService.processLogin(username, phone, email, password, Optional.ofNullable(RequestUtils.getConcurrentHeader(CommonVar.HUMAN_VERIFY_HEADER_KEY)).orElse(""));
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
    @OpLog(operateName = OpEnum.UPDATEUSERINFO,recordClass = OpRecordMybatisWrapper.class )
    public UserBase saveUserBaseInfo(@ModelAttribute @Validated UserBase userBase)
    {
        DemonstrationSystemUtils.ban();
        try {
            return userBaseService.addOrUpdateUser(userBase);
        }catch (DuplicateKeyException exception)
        {
            throw new UserIsExitsException();
        }
    }


    @PostMapping("/saveUserExtendInfo")
    @PreAuthorize("hasAnyRole('user_user_query')")
    @OpLog(operateName = OpEnum.UPDATEUSERINFO,recordClass = OpRecordMybatisWrapper.class )
    public UserExtend saveUserExtendInfo(@ModelAttribute UserExtend userExtend)
    {
        DemonstrationSystemUtils.ban();
        return userExtendService.addOrUpdateUserExtend(userExtend);
    }


    @PostMapping("/setUserDisable")
    @PreAuthorize("hasAnyRole('user_user_query')")
    @OpLog(operateName = OpEnum.UPDATEUSERINFO,recordClass = OpRecordMybatisWrapper.class )
    @Auth2FA(require = true)
    public Boolean setUserDisable(@RequestParam @Length(min = 3,max = 20,message = "用户名长度3-20") String username
            ,@RequestParam Integer disable)
    {
        DemonstrationSystemUtils.ban();
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
    @OpLog(operateName = OpEnum.UPDATEUSERINFO,recordClass = OpRecordMybatisWrapper.class )
    public Boolean resetPassword(@RequestParam @Length(min = 3,max = 20,message = "用户名长度3-20") String username
            , @RequestParam Boolean isPwd)
    {
        DemonstrationSystemUtils.ban();
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
    @OpLog(operateName = OpEnum.DELETEUSERINFO,recordClass = OpRecordMybatisWrapper.class )
    public Boolean deleteUser(@RequestParam @Length(min = 3,max = 20,message = "用户名长度3-20") String username)
    {
        DemonstrationSystemUtils.ban();
        try {
            UserBase user = userBaseService.lambdaQuery().eq(UserBase::getUsername, username).one();
            boolean res = false;
            if (Objects.nonNull(user))
            {
                AssertUrils.state(user.getId() > 1000L, DefaultRoleDeleteError.class);
                res = userBaseService.lambdaUpdate().eq(UserBase::getId,user.getId()).remove();
                userExtendService.lambdaUpdate().eq(UserExtend::getUserId,user.getId()).remove();
            }
            return res;
        }catch (Exception exception)
        {
            if (exception instanceof DefaultRoleDeleteError)
                throw exception;
            log.error(exception.getMessage());
            throw new SqlRunException(R.ErrorMessage.SQL_RUN_ERROR.get());
        }
    }


    @GetMapping("/userIsFirstLogin")
    @PreAuthorize("hasAnyRole('microservice_call','get_own_information')")
    public Boolean userIsFirstLogin()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && StringUtils.hasText(authentication.getName()))
        {
            UserBase user = userBaseService.lambdaQuery().select(UserBase::getId)
                    .eq(UserBase::getUsername, authentication.getName()).one();
            if (user!=null)
            {
                List<UserLoginEventRecord> l = loginEventRecordService.lambdaQuery().select(UserLoginEventRecord::getId)
                        .eq(UserLoginEventRecord::getUserId, user.getId()).last("limit 2").list();
                return l.size() < 2;
            }
        }
        return false;
    }


    @GetMapping("/getWsOnlineUsersForMs")
    @PreAuthorize("hasAnyRole('microservice_call')")
    public List<String> getWsOnlineUsersName(){
        return WsCache.getAllAuthConnect().stream().map(WsClient::getUsername).distinct().toList();
    }

    @GetMapping("/getWsOnlineUsersDataForMs")
    @PreAuthorize("hasAnyRole('microservice_call')")
    public List<OnlineUserInfo> getWsOnlineUsersDataForMs(){
        return WsCache.getAllAuthConnect().stream().map(e->{
            OnlineUserInfo onlineUserInfo = new OnlineUserInfo();
            onlineUserInfo.setUsername(e.getUsername());
            onlineUserInfo.setSessionId(e.getSessionId());
            onlineUserInfo.setIp(e.getIp());
            onlineUserInfo.setSystem(e.getSystem());
            return onlineUserInfo;
        }).toList();
    }

    @GetMapping("/getWsOnlineUsersData")
    @EncryResult
    public WebAsyncTask<List<OnlineUserInfo>> getWsOnlineUsersData(){
        return new WebAsyncTask<>(userBaseService::getAllWsOnlineUsersData);
    }


    @GetMapping("/getWsOnlineUsersForMsTest")
    @WriteList
    @Deprecated
    public List<String> getWsOnlineUsersForMsTest(){
        return WsCache.getAllAuthConnect().stream().map(WsClient::getUsername).distinct().toList();
    }

    @GetMapping("/getWsOnlineUsersForMsTest1")
    @WriteList
    @Deprecated
    public List getWsOnlineUsersForMsTest1(){
        ConcurrentMap<String, WsClient> map = WsCache.authConnect.asMap();
        List res = new ArrayList();
        map.values().stream().forEach(e->{
            res.add(MapBuilder.getInstance(true)
                    .add("username",e.getUsername())
                    .add("sessionId",e.getSessionId())
                    .add("ip",e.getIp())
                    .add("system",e.getSystem())
                    .add("token",e.getToken()).build());
        });
        return res;
    }



    @GetMapping("/uuuutest")
    @WriteList
    @LimitResource("def")
    public String uuuutest(){
        return Thread.currentThread().getName();
    }

}
