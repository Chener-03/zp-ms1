package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.feign.loadbalance.LoadbalancerContextHolder;
import xyz.chener.zp.common.config.feign.loadbalance.ServerInstance;
import xyz.chener.zp.common.entity.DictionaryKeyEnum;
import xyz.chener.zp.common.entity.LoginUserDetails;
import xyz.chener.zp.common.entity.SecurityVar;
import xyz.chener.zp.common.utils.*;
import xyz.chener.zp.zpusermodule.dao.UserBaseDao;
import xyz.chener.zp.zpusermodule.entity.*;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.OwnInformation;
import xyz.chener.zp.zpusermodule.entity.dto.ResetPasswordDto;
import xyz.chener.zp.zpusermodule.entity.dto.UserAllInfoDto;
import xyz.chener.zp.zpusermodule.error.user.UserDisableException;
import xyz.chener.zp.zpusermodule.error.user.UserExpireException;
import xyz.chener.zp.zpusermodule.error.user.UserNotFoundException;
import xyz.chener.zp.zpusermodule.error.user.UsernamePasswordErrorException;
import xyz.chener.zp.zpusermodule.service.DictionariesService;
import xyz.chener.zp.zpusermodule.service.GoogleRecapthaService;
import xyz.chener.zp.zpusermodule.service.UserBaseService;
import xyz.chener.zp.zpusermodule.service.UserModuleService;

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
    public LoginResult processUsernameLogin(String username, String password) {
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
            LoginUserDetails details = new LoginUserDetails();
            details.setSystem(LoginUserDetails.SystemEnum.WEB);
            details.setUsername(userBase.getUsername());
            details.setDs(userBase.getDs());
            res.setLastLoginTime(userBase.getLastLoginTime());
            res.setLastLoginIp(userBase.getLastLoginIp());
            details.setIp(ip);
            details.setOs(os);
            String encode = jwt.encode(details);
            userBase.setLastLoginIp(ip);
            userBase.setLastLoginOs(os);
            Date date = new Date();
            userBase.setLastLoginTime(date);

            TransactionDefinition td = TransactionUtils.getTransactionDefinition();
            TransactionStatus transaction = dataSourceTransactionManager.getTransaction(td);
            try{
                uler.setTime(date);
                uler.setIsSuccess(1);
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
            return processUsernameLogin(username,password);

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
        if (serverInstance.size() == 0)
            return Collections.emptyList();
        ArrayList<String > res = new ArrayList<>();
        serverInstance.forEach(e->{
            LoadbalancerContextHolder.setNextInstance(e);
            List<String> wsOnlineUsersName = userModuleService.getWsOnlineUsersName();
            res.addAll(wsOnlineUsersName);
        });
        return res;
    }

}

