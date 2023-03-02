package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.dao.UserExtendDao;
import xyz.chener.zp.zpusermodule.entity.UserExtend;
import xyz.chener.zp.zpusermodule.service.UserExtendService;

/**
 * (UserExtend)表服务实现类
 *
 * @author makejava
 * @since 2023-01-19 15:19:57
 */
@Service
public class UserExtendServiceImpl extends ServiceImpl<UserExtendDao, UserExtend> implements UserExtendService {

    @Override
    public UserExtend addOrUpdateUserExtend(UserExtend userExtend) {
        UserExtend ue = lambdaQuery().eq(UserExtend::getUserId, userExtend.getUserId()).one();
        if (ue != null)
        {
            if (ObjectUtils.objectFieldsEquals(ue, userExtend,UserExtend::getAvatarId
                    ,UserExtend::getEmail,UserExtend::getPhone
                    ,UserExtend::getPost,UserExtend::getAutograph
                    ,UserExtend::getNameCn,UserExtend::getIntroduce))
            {
                return ue;
            }
            return lambdaUpdate().set(UserExtend::getEmail,userExtend.getEmail())
                    .set(UserExtend::getAvatarId,userExtend.getAvatarId())
                    .set(UserExtend::getPhone,userExtend.getPhone())
                    .set(UserExtend::getPost,userExtend.getPost())
                    .set(UserExtend::getAutograph,userExtend.getAutograph())
                    .set(UserExtend::getNameCn,userExtend.getNameCn())
                    .set(UserExtend::getIntroduce,userExtend.getIntroduce())
                    .eq(UserExtend::getUserId,userExtend.getUserId()).update()?userExtend:null;
        }else
        {
            save(userExtend);
            return userExtend;
        }
    }
}

