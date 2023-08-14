package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.Auth2FAUtils;
import xyz.chener.zp.zpusermodule.dao.User2faDao;
import xyz.chener.zp.zpusermodule.entity.User2fa;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.Auth2FaMessageDto;
import xyz.chener.zp.zpusermodule.error.fa.Auth2FaAlreadyEnable;
import xyz.chener.zp.zpusermodule.error.fa.Auth2FaVerifyFail;
import xyz.chener.zp.zpusermodule.error.user.UserNotFoundException;
import xyz.chener.zp.zpusermodule.service.User2faService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.service.UserBaseService;

import java.util.*;

/**
 * (User2fa)表服务实现类
 *
 * @author makejava
 * @since 2023-07-25 20:21:47
 */
@Service
public class User2faServiceImpl extends ServiceImpl<User2faDao, User2fa> implements User2faService {

    private UserBaseService userBaseService;

    @Autowired
    public void setUserBaseService(UserBaseService userBaseService) {
        this.userBaseService = userBaseService;
    }

    @Override
    public Auth2FaMessageDto enable2Fa(String username) {
        UserBase user = userBaseService.lambdaQuery()
                .select(UserBase::getUsername,UserBase::getId)
                .eq(UserBase::getUsername, username).one();
        AssertUrils.state(user != null, UserNotFoundException.class);

        Long count = lambdaQuery().eq(User2fa::getUserId, user.getId()).count();
        AssertUrils.state(count == 0, Auth2FaAlreadyEnable.class);

        Auth2FaMessageDto res = new Auth2FaMessageDto();
        String key = Auth2FAUtils.TwoFactorAuthenticator.generateSecretKey();
        String qrcode = Auth2FAUtils.TwoFactorAuthenticator.generateQrCode(key);
        res.setKey(key);
        res.setUrl(qrcode);
        return res;
    }

    @Override
    public Auth2FaMessageDto confirmEnable2Fa(Auth2FaMessageDto code, String username) {

        UserBase user = userBaseService.lambdaQuery()
                .select(UserBase::getUsername,UserBase::getId)
                .eq(UserBase::getUsername, username).one();
        AssertUrils.state(user != null, UserNotFoundException.class);

        Long count = lambdaQuery().eq(User2fa::getUserId, user.getId()).count();
        AssertUrils.state(count == 0, Auth2FaAlreadyEnable.class);

        boolean success = Auth2FAUtils.TwoFactorAuthenticator.verifyCode(code.getCode(), code.getKey());
        AssertUrils.state(success, Auth2FaVerifyFail.class);

        String codes = randomDisposableCode();
        User2fa user2fa = new User2fa();
        user2fa.setUserId(user.getId());
        user2fa.setTotpSecretKey(code.getKey());
        user2fa.setBakKey(codes);
        save(user2fa);

        Auth2FaMessageDto res = new Auth2FaMessageDto();
        res.setSuccess(true);
        res.setCode(codes);
        return res;
    }

    @Override
    public Boolean verify2Fa(String code, String username) {
        UserBase user = userBaseService.lambdaQuery()
                .select(UserBase::getUsername,UserBase::getId)
                .eq(UserBase::getUsername, username).one();
       if (user == null){
           return false;
       }

        User2fa user2fa = lambdaQuery().eq(User2fa::getUserId, user.getId()).one();
        if (user2fa == null){
            return true;
        }

        List<String> bak2faCode = Arrays.stream(user2fa.getBakKey().split("[|]"))
                .toList();
        if (bak2faCode.contains(code)){
            ArrayList<String> l = new ArrayList<>(bak2faCode);
            l.remove(code);
            user2fa.setBakKey(String.join("|",l));
            updateById(user2fa);
            return true;
        }
        return Auth2FAUtils.TwoFactorAuthenticator.verifyCode(code, user2fa.getTotpSecretKey());
    }

    @Override
    public Boolean disable2Fa(String code, String username) {
        UserBase user = userBaseService.lambdaQuery()
                .select(UserBase::getUsername,UserBase::getId)
                .eq(UserBase::getUsername, username).one();
        if (user == null){
            return false;
        }

        int delete = this.getBaseMapper().delete(lambdaUpdate().eq(User2fa::getUserId, user.getId()).getWrapper());
        return delete > 0;
    }


    private String randomDisposableCode(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            res.add(uuid.substring(i*6,(i*6)+6));
        }
        return String.join("|",res);
    }


}

