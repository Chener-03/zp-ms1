package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.feign.loadbalance.LoadbalancerContextHolder;
import xyz.chener.zp.common.config.feign.loadbalance.ServerInstance;
import xyz.chener.zp.common.entity.*;
import xyz.chener.zp.common.utils.*;
import xyz.chener.zp.zpusermodule.dao.UserBaseDao;
import xyz.chener.zp.zpusermodule.entity.*;
import xyz.chener.zp.zpusermodule.entity.dto.*;
import xyz.chener.zp.zpusermodule.error.fa.Auth2FaNeedVerify;
import xyz.chener.zp.zpusermodule.error.user.UserDisableException;
import xyz.chener.zp.zpusermodule.error.user.UserExpireException;
import xyz.chener.zp.zpusermodule.error.user.UserNotFoundException;
import xyz.chener.zp.zpusermodule.error.user.UsernamePasswordErrorException;
import xyz.chener.zp.zpusermodule.service.*;

import java.util.*;

/**
 * (UserBase)表服务实现类
 *
 * @author makejava
 * @since 2023-01-11 15:23:15
 */
@Service
@Slf4j
public class UserBaseServiceImpl extends ServiceImpl<UserBaseDao, UserBase> implements UserBaseService {

    private final Jwt jwt;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserLoginEventRecordServiceImpl userLoginEventRecordService;
    private final DataSourceTransactionManager dataSourceTransactionManager;
    private final UserExtendServiceImpl userExtendService;
    private final RoleServiceImpl roleService;
    private final DictionariesService dictionariesService;

    private final GoogleRecapthaService googleRecapthaService;
    private final NacosUtils nacosUtils;
    private final UserModuleService userModuleService;

    private User2faService user2faService;


    @Autowired
    @Lazy
    public void setUser2faService(User2faService user2faService) {
        this.user2faService = user2faService;
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.cloud.nacos.discovery.group}")
    private String applicationNacosGroup;


    public UserBaseServiceImpl(Jwt jwt, BCryptPasswordEncoder bCryptPasswordEncoder, UserLoginEventRecordServiceImpl userLoginEventRecordService, DataSourceTransactionManager dataSourceTransactionManager, UserExtendServiceImpl userExtendService, RoleServiceImpl roleService, DictionariesService dictionariesService, GoogleRecapthaService googleRecapthaService, NacosUtils nacosUtils
            ,@Qualifier("xyz.chener.zp.zpusermodule.service.UserModuleService") UserModuleService userModuleService) {
        this.jwt = jwt;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userLoginEventRecordService = userLoginEventRecordService;
        this.dataSourceTransactionManager = dataSourceTransactionManager;
        this.userExtendService = userExtendService;
        this.roleService = roleService;
        this.dictionariesService = dictionariesService;
        this.googleRecapthaService = googleRecapthaService;
        this.nacosUtils = nacosUtils;
        this.userModuleService = userModuleService;
    }

    @Override
    public LoginResult processUsernameLogin(String username, String password,String systemEnum) {
        LoginResult res = new LoginResult();
        res.setSuccess(false);
        UserLoginEventRecord uler = new UserLoginEventRecord();

        try {
            String ip = RequestUtils.getConcurrentIp();
            String os = RequestUtils.getConcurrentOs();
            uler.setIp(ip);
            uler.setOs(os);

            UserBase userBase = lambdaQuery().eq(UserBase::getUsername, username).one();
            AssertUrils.state(Objects.nonNull(userBase),UserNotFoundException.class);
            uler.setUserId(userBase.getId());
            AssertUrils.state(bCryptPasswordEncoder.matches(password,userBase.getPassword()),UsernamePasswordErrorException.class);
            AssertUrils.state(userBase.getDisable() == 0 , UserDisableException.class);
            AssertUrils.state(userBase.getExpireTime().getTime() > new Date().getTime(), UserExpireException.class);

            //todo: check 2fa
            HttpServletRequest concurrentRequest = RequestUtils.getConcurrentRequest();
            String faheader = concurrentRequest.getHeader(CommonVar.FA_HEADER_KEY);

            Integer fares = user2faService.verify2Fa(Optional.ofNullable(faheader).orElse(""), userBase.getUsername(), false, faheader != null);
            AssertUrils.state(fares == Auth2FaRegisterMetadata.AuthResultCode.SUCCESS, Auth2FaNeedVerify.class);


            LoginUserDetails details = new LoginUserDetails();
            details.setSystem(systemEnum);
            details.setUsername(userBase.getUsername());
            details.setDs(userBase.getDs());
            res.setLastLoginTime(userBase.getLastLoginTime());
            res.setLastLoginIp(userBase.getLastLoginIp());
            details.setIp(ip);
            details.setOs(os);
            String encode = jwt.encode(details,systemEnum);
            userBase.setLastLoginIp(ip);
            userBase.setLastLoginOs(os);
            Date date = new Date();
            userBase.setLastLoginTime(date);

            TransactionDefinition td = TransactionUtils.getTransactionDefinition();
            TransactionStatus transaction = dataSourceTransactionManager.getTransaction(td);
            try{
                uler.setTime(date);
                uler.setIsSuccess(1);
                uler.setLoginType(UserLoginTypeEnum.USERNAMEPASSWORD);
                uler.setLoginOsType(systemEnum);
                userLoginEventRecordService.save(uler);
                AssertUrils.state(this.updateById(userBase),new RuntimeException(LoginResult.ErrorResult.SERVER_ERROR));
                dataSourceTransactionManager.commit(transaction);
                res.setSuccess(true);
                res.setToken(encode);
            }catch (Exception exception)
            {
                dataSourceTransactionManager.rollback(transaction);
                throw new RuntimeException(LoginResult.ErrorResult.SERVER_ERROR);
            }
        }catch (Exception exception)
        {
            if (exception instanceof Auth2FaNeedVerify)
                throw exception;
            log.warn(exception.getMessage());
            res.setSuccess(false);
            res.setMessage(exception.getMessage());
            if (exception instanceof TooManyResultsException
                    || exception instanceof UserNotFoundException
                    || exception instanceof UsernamePasswordErrorException)
            {
                res.setMessage(LoginResult.ErrorResult.USERNAME_PASSWORD_ERROR);
            }

            if (Objects.nonNull(uler.getUserId()))
            {
                uler.setFailReason(exception.getMessage());
                uler.setTime(new Date());
                uler.setIsSuccess(0);
                userLoginEventRecordService.save(uler);
            }
        }
        return res;
    }

    @Override
    public LoginResult processLogin(String username, String phone, String email
            , String password,String verification) {
        if (!checkGoogleVerification(verification))
        {
            LoginResult loginResult = new LoginResult();
            loginResult.setSuccess(false);
            loginResult.setMessage(LoginResult.ErrorResult.GOOGLE_FALSE);
            return loginResult;
        }

        if (StringUtils.hasText(username))
            return processUsernameLogin(username,password, LoginUserDetails.SystemEnum.WEB);

        return null;
    }


    @Override
    public boolean checkGoogleVerification(String code) {
        return googleRecapthaService.check(code);
    }

    @Override
    public OwnInformation getUserInformation(String username) {
        OwnInformation information = new OwnInformation();
        try {
            UserBase userBase = lambdaQuery().select(UserBase.class
                            , tableFieldInfo -> !tableFieldInfo.getColumn().equals("password")
                    && !tableFieldInfo.getColumn().equals("ds"))
                    .eq(UserBase::getUsername, username).one();
            Objects.requireNonNull(userBase);
            UserExtend userExtend = userExtendService.lambdaQuery().eq(UserExtend::getUserId, userBase.getId()).one();
            information.setUserBase(userBase);
            information.setUserExtend(userExtend);
            Role role = roleService.lambdaQuery().eq(Role::getId, userBase.getRoleId()).one();
            if (Objects.nonNull(role))
            {
                information.getRoleList()
                        .addAll(Arrays.stream(role.getPermissionEnNameList().split("[,]"))
                                .filter(StringUtils::hasText)
                                .map(ns->{
                                    if (ns.indexOf(SecurityVar.ROLE_PREFIX)==0)
                                        return ns.substring(SecurityVar.ROLE_PREFIX.length());
                                    if (ns.indexOf(SecurityVar.UI_PREFIX)==0)
                                        return ns.substring(SecurityVar.UI_PREFIX.length());
                                    return ns;
                                }).toList());
            }
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
        }
        return information;
    }

    @Override
    public PageInfo<UserAllInfoDto> getUserAllInfo(UserAllInfoDto userAllInfo
            , Integer page, Integer size,Boolean roleNotNull) {
        PageHelper.startPage(page,size);
        userAllInfo.startQuery();
        List<UserAllInfoDto> info = getBaseMapper().getAllUserInfo(userAllInfo,roleNotNull);
        info.stream().parallel().forEach(e-> e.addRoleName(roleService));
        return new PageInfo<>(info);
    }

    @Override
    public UserBase addOrUpdateUser(UserBase userBase) {
        if (userBase.getId()== null) {
            Dictionaries roleId = dictionariesService.lambdaQuery().eq(Dictionaries::getId, DictionaryKeyEnum.DEFAULT_ROLE.get()).one();
            Dictionaries pwd = dictionariesService.lambdaQuery().eq(Dictionaries::getId, DictionaryKeyEnum.DEFAULT_PASSWORD.get()).one();
            String rId = Optional.ofNullable(roleId).orElseGet(Dictionaries::new).getValue0();
            userBase.setRoleId(Objects.nonNull(rId) ? Long.parseLong(rId) : null);
            userBase.setPassword(Optional.ofNullable(pwd).orElseGet(Dictionaries::new).getValue0());
            userBase.setCreateTime(new Date());
            userBase.setDs(UUID.randomUUID().toString());
            return this.save(userBase)? userBase : null;
        }
        UserBase user = lambdaQuery().eq(UserBase::getId, userBase.getId()).one();
        if (Objects.nonNull(user)  && ObjectUtils.objectFieldsEquals(user,userBase,UserBase::getUsername
                ,UserBase::getExpireTime,UserBase::getDisable))
        {
            return userBase;
        }

        return lambdaUpdate().set(UserBase::getUsername,userBase.getUsername())
                .set(UserBase::getExpireTime,userBase.getExpireTime())
                .set(UserBase::getDisable,userBase.getDisable())
                .eq(UserBase::getId,userBase.getId()).update()? userBase : null;

    }

    @Override
    public ResetPasswordDto resetPassword(String username, String newPassword, String oldPassword) {
        ResetPasswordDto res = new ResetPasswordDto();
        UserBase user = lambdaQuery().select(UserBase::getPassword)
                .eq(UserBase::getUsername, username).one();
        if (!bCryptPasswordEncoder.matches(oldPassword,user.getPassword())) {
            res.setSuccess(false);
            res.setMessage("旧密码错误");
            return res;
        }
        var b = lambdaUpdate().set(UserBase::getPassword, bCryptPasswordEncoder.encode(newPassword))
                .set(UserBase::getDs, UUID.randomUUID().toString())
                .eq(UserBase::getUsername, username).update();
        res.setSuccess(b);
        return res;
    }


    @Override
    public List<String> getAllWsOnlineUsersName() {
        List<ServerInstance> serverInstance = nacosUtils.getServerInstance(applicationName, applicationNacosGroup);
        if (serverInstance.isEmpty())
            return Collections.emptyList();
        ArrayList<String > res = new ArrayList<>();
        serverInstance.forEach(e->{
            LoadbalancerContextHolder.setNextInstance(e);
            List<String> wsOnlineUsersName = userModuleService.getWsOnlineUsersName();
            res.addAll(wsOnlineUsersName);
        });
        return res;
    }

    @Override
    public List<OnlineUserInfo> getAllWsOnlineUsersData() {
        List<ServerInstance> serverInstance = nacosUtils.getServerInstance(applicationName, applicationNacosGroup);
        if (serverInstance.isEmpty())
            return Collections.emptyList();
        ArrayList<OnlineUserInfo> res = new ArrayList<>();
        serverInstance.forEach(e->{
            LoadbalancerContextHolder.setNextInstance(e);
            List<OnlineUserInfo> wsOnlineUsersDataForMs = userModuleService.getWsOnlineUsersDataForMs();
            wsOnlineUsersDataForMs.forEach(dt->{
                dt.setSessionId(e.host().replaceAll("[.]","") + e.port() +"-"+dt.getSessionId());
                res.add(dt);
            });
        });
        return res;
    }

    @Override
    public LoginResult doLoginClient(String username, String password) {
        // recapcha Android 国内无代理 跳过验证
        return processUsernameLogin(username,password, LoginUserDetails.SystemEnum.CLIENT);
    }

}

