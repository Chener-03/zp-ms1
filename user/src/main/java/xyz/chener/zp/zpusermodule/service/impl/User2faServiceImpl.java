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
import xyz.chener.zp.zpusermodule.error.user.UserNotFoundException;
import xyz.chener.zp.zpusermodule.service.User2faService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.service.UserBaseService;

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
}

