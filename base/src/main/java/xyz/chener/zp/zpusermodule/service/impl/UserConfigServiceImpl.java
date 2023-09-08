package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.zpusermodule.dao.UserConfigDao;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.UserConfig;
import xyz.chener.zp.zpusermodule.service.UserBaseService;
import xyz.chener.zp.zpusermodule.service.UserConfigService;
import org.springframework.stereotype.Service;

/**
 * (UserConfig)表服务实现类
 *
 * @author makejava
 * @since 2023-03-11 19:12:35
 */
@Service
public class UserConfigServiceImpl extends ServiceImpl<UserConfigDao, UserConfig> implements UserConfigService {

    private UserBaseService userBaseService;

    @Autowired
    public void setUserBaseService(UserBaseService userBaseService) {
        this.userBaseService = userBaseService;
    }

    @Override
    public UserConfig getUserConfig(String username, FieldQuery fieldQuery) {
        UserBase user = userBaseService.lambdaQuery().select(UserBase::getId, UserBase::getUsername)
                .eq(UserBase::getUsername, username).one();
        QueryHelper.StartQuery(fieldQuery, UserConfig.class);
        return this.lambdaQuery().eq(UserConfig::getUserId, user.getId()).one();
    }

    @Override
    public Boolean updateConcurrentUserLayoutConfig(String username, String layoutConfig) {
        UserBase user = userBaseService.lambdaQuery().select(UserBase::getId, UserBase::getUsername)
                .eq(UserBase::getUsername, username).one();
        UserConfig userConfig = this.lambdaQuery().select(UserConfig::getUserId).eq(UserConfig::getUserId, user.getId()).one();
        if (userConfig == null) {
            userConfig = new UserConfig();
            userConfig.setUserId(user.getId());
            userConfig.setConfigJsonLayout(layoutConfig);
            return this.save(userConfig);
        } else {
            return lambdaUpdate().set(UserConfig::getConfigJsonLayout, layoutConfig)
                    .eq(UserConfig::getUserId, user.getId()).update();
        }
    }
}

