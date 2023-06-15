package xyz.chener.zp.zpusermodule.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import xyz.chener.zp.common.config.feign.loadbalance.LoadbalancerContextHolder;
import xyz.chener.zp.common.config.feign.loadbalance.ServerInstance;
import xyz.chener.zp.common.entity.LoginUserDetails;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.Jwt;
import xyz.chener.zp.common.utils.TransactionUtils;
import xyz.chener.zp.zpusermodule.entity.QrCodeLoginCache;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.UserLoginEventRecord;
import xyz.chener.zp.zpusermodule.entity.UserLoginTypeEnum;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.QrCodeLoginRespDto;
import xyz.chener.zp.zpusermodule.service.QrCodeLoginService;
import xyz.chener.zp.zpusermodule.service.UserBaseService;
import xyz.chener.zp.zpusermodule.service.UserModuleService;
import xyz.chener.zp.zpusermodule.utils.Ip2RegUtils;
import xyz.chener.zp.zpusermodule.ws.WsCache;
import xyz.chener.zp.zpusermodule.ws.WsConnector;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessage;
import xyz.chener.zp.zpusermodule.ws.entity.WsMessageConstVar;

import java.util.Date;
import java.util.Objects;

/**
 * @Author: chenzp
 * @Date: 2023/06/15/11:53
 * @Email: chen@chener.xyz
 */


@Service
public class QrCodeLoginServiceImpl implements QrCodeLoginService {

    private final RedissonClient redissonClient;

    private final ServerProperties serverProperties;
    private final Ip2RegUtils ip2RegUtils ;

    private final UserModuleService userModuleService;

    private final UserBaseService userBaseService;
    private final Jwt jwt;

    private final DataSourceTransactionManager dataSourceTransactionManager;

    private final UserLoginEventRecordServiceImpl userLoginEventRecordService;

    @Value("${spring.cloud.nacos.discovery.ip}")
    private String regIp;

    public QrCodeLoginServiceImpl(RedissonClient redissonClient, ServerProperties serverProperties, Ip2RegUtils ip2RegUtils
            , @Qualifier("xyz.chener.zp.zpusermodule.service.UserModuleService") UserModuleService userModuleService, UserBaseService userBaseService, Jwt jwt, DataSourceTransactionManager dataSourceTransactionManager, UserLoginEventRecordServiceImpl userLoginEventRecordService) {
        this.redissonClient = redissonClient;
        this.serverProperties = serverProperties;
        this.ip2RegUtils = ip2RegUtils;
        this.userModuleService = userModuleService;
        this.userBaseService = userBaseService;
        this.jwt = jwt;
        this.dataSourceTransactionManager = dataSourceTransactionManager;
        this.userLoginEventRecordService = userLoginEventRecordService;
    }


    @Override
    public boolean putQrCodeLogin(String uuid, String sessionId,String ip,String os) {
        QrCodeLoginCache cache = new QrCodeLoginCache();
        cache.setUuid(uuid);
        cache.setSessionId(sessionId);
        cache.setHost(regIp);
        cache.setPort(serverProperties.getPort());
        cache.setIpAddr(ip);
        cache.setOs(os);
        try {
            redissonClient.getBucket(QRCODE_LOGIN_CACHE_KEY+uuid)
                    .set(cache,EXPIRE_TIME,java.util.concurrent.TimeUnit.SECONDS);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public QrCodeLoginRespDto qrCodeGet(String uuid) {
        QrCodeLoginRespDto res = new QrCodeLoginRespDto();
        Object o = redissonClient.getBucket(QrCodeLoginService.QRCODE_LOGIN_CACHE_KEY + uuid).get();
        if(Objects.nonNull(o) && o instanceof QrCodeLoginCache cache){
            res.setUuid(uuid);
            res.setSuccess(true);
            res.setIp(cache.getIpAddr());
            res.setOs(cache.getOs());
            Ip2RegUtils.Reg reg = ip2RegUtils.getReg(cache.getIpAddr());
            res.setAddressName(reg.toString());
            LoadbalancerContextHolder.setNextInstance(new ServerInstance(cache.getHost(), cache.getPort()));
            if (!userModuleService.postQrCodeLoginGet(cache.getSessionId())) {
                res.setSuccess(false);
            }
        }else {
            res.setSuccess(false);
        }
        return res;
    }

    @Override
    public boolean postQrCodeLoginGet(String sessionId) {
        WsMessage msg = new WsMessage();
        msg.setCode(WsMessageConstVar.QRCODE_LOGIN_READY);
        if (WsCache.qrCodeLoginConnect.asMap().containsKey(sessionId)) {
            WsConnector.sendObject(msg,sessionId);
            return true;
        }
        return false;
    }

    @Override
    public Boolean qrCodeAuthorization(String uuid, String username) {
        Object o = redissonClient.getBucket(QrCodeLoginService.QRCODE_LOGIN_CACHE_KEY + uuid).getAndDelete();
        if(Objects.nonNull(o) && o instanceof QrCodeLoginCache cache){
            UserBase userBase = userBaseService.lambdaQuery().eq(UserBase::getUsername, username).one();
            LoginResult res = new LoginResult();
            if (Objects.isNull(userBase)) {
                res.setSuccess(false);
                res.setMessage(LoginResult.ErrorResult.NO_USER);
                sendQrCodeResult(res,cache.getSessionId(), cache.getHost(), cache.getPort());
                return false;
            }
            LoginUserDetails details = new LoginUserDetails();

            details.setSystem(LoginUserDetails.SystemEnum.WEB);
            details.setUsername(userBase.getUsername());
            details.setDs(userBase.getDs());
            res.setLastLoginTime(userBase.getLastLoginTime());
            res.setLastLoginIp(userBase.getLastLoginIp());
            details.setIp(cache.getIpAddr());
            details.setOs(cache.getOs());
            String encode = jwt.encode(details);
            userBase.setLastLoginIp(cache.getIpAddr());
            userBase.setLastLoginOs(cache.getOs());
            Date date = new Date();
            userBase.setLastLoginTime(date);

            TransactionDefinition td = TransactionUtils.getTransactionDefinition();
            TransactionStatus transaction = dataSourceTransactionManager.getTransaction(td);
            try{
                UserLoginEventRecord uler = new UserLoginEventRecord();
                uler.setIp(cache.getIpAddr());
                uler.setOs(cache.getOs());
                uler.setUserId(userBase.getId());
                uler.setTime(date);
                uler.setIsSuccess(1);
                uler.setLoginType(UserLoginTypeEnum.QRCODE);
                userLoginEventRecordService.save(uler);
                AssertUrils.state(userBaseService.updateById(userBase),new RuntimeException(LoginResult.ErrorResult.SERVER_ERROR));
                dataSourceTransactionManager.commit(transaction);
                res.setSuccess(true);
                res.setToken(encode);
            }catch (Exception exception)
            {
                dataSourceTransactionManager.rollback(transaction);
                res.setSuccess(false);
                res.setMessage(LoginResult.ErrorResult.SERVER_ERROR);
            }
            sendQrCodeResult(res,cache.getSessionId(), cache.getHost(), cache.getPort());
            return true;
        }
        return false;
    }

    @Override
    public boolean postQrCodeLoginAuthorization(String sessionId, LoginResult result) {
        WsMessage msg = new WsMessage();
        msg.setCode(WsMessageConstVar.QRCODE_LOGIN_DOLOGIN);
        try {
            msg.setMessage(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException ignored) {}
        if (WsCache.qrCodeLoginConnect.asMap().containsKey(sessionId)) {
            WsConnector.sendObject(msg,sessionId);
            return true;
        }
        return false;
    }

    private void sendQrCodeResult(LoginResult result,String sessionId,String host,Integer port){
        LoadbalancerContextHolder.setNextInstance(new ServerInstance(host, port));
        userModuleService.postQrCodeLoginAuthorization(sessionId,result);
    }

}
