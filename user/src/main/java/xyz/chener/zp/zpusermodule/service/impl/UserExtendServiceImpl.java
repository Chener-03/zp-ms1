package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.dao.OrgUserMapDao;
import xyz.chener.zp.zpusermodule.dao.UserExtendDao;
import xyz.chener.zp.zpusermodule.entity.OrgBase;
import xyz.chener.zp.zpusermodule.entity.Role;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.UserExtend;
import xyz.chener.zp.zpusermodule.entity.dto.UserOtherInfo;
import xyz.chener.zp.zpusermodule.error.user.UserNotFoundException;
import xyz.chener.zp.zpusermodule.service.OrgBaseService;
import xyz.chener.zp.zpusermodule.service.RoleService;
import xyz.chener.zp.zpusermodule.service.UserBaseService;
import xyz.chener.zp.zpusermodule.service.UserExtendService;

/**
 * (UserExtend)表服务实现类
 *
 * @author makejava
 * @since 2023-01-19 15:19:57
 */
@Service
public class UserExtendServiceImpl extends ServiceImpl<UserExtendDao, UserExtend> implements UserExtendService {

    private UserBaseService userBaseService;
    private final RoleService roleService;
    private OrgBaseService orgBaseService;

    private OrgUserMapDao orgUserMapDao;

    @Autowired
    @Lazy
    public void setUserBaseService(UserBaseService userBaseService) {
        this.userBaseService = userBaseService;
    }

    @Autowired
    @Lazy
    public void setOrgBaseService(OrgBaseService orgBaseService) {
        this.orgBaseService = orgBaseService;
    }

    @Autowired
    @Lazy
    public void setOrgUserMapDao(OrgUserMapDao orgUserMapDao) {
        this.orgUserMapDao = orgUserMapDao;
    }

    public UserExtendServiceImpl(RoleService roleService) {
        this.roleService = roleService;
    }

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

    @Override
    public UserOtherInfo getSelfOtherInfo(String username) {
        UserBase user = userBaseService.lambdaQuery().select(UserBase::getId, UserBase::getRoleId)
                .eq(UserBase::getUsername, username).one();
        AssertUrils.state(user != null, UserNotFoundException.class);
        Role role = roleService.lambdaQuery().select(Role::getRoleName).eq(Role::getId, user.getRoleId()).one();
        UserOtherInfo res = new UserOtherInfo();
        res.setRoleName(role.getRoleName());
        res.setOrgName(getOrgNameByUserId(user.getId()));
        return res;
    }

    private String getOrgNameByUserId(Long userId)
    {
        OrgBase org = orgUserMapDao.getOrgBaseByUserId(userId);
        if (org == null)
            return null;
        StringBuilder orgFullPathName = new StringBuilder(org.getOrgChName());
        while (org.getParentId() != null)
        {
            org = orgBaseService.lambdaQuery().select(OrgBase::getOrgChSimpleName, OrgBase::getParentId)
                    .eq(OrgBase::getId, org.getParentId()).one();
            orgFullPathName.insert(0, org.getOrgChSimpleName() + "/");
        }
        return orgFullPathName.toString();
    }
}

