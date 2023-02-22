package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.dao.OrgBaseDao;
import xyz.chener.zp.zpusermodule.entity.OrgBase;
import xyz.chener.zp.zpusermodule.entity.Role;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.UserExtend;
import xyz.chener.zp.zpusermodule.entity.dto.OrgInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgTreeDto;
import xyz.chener.zp.zpusermodule.service.OrgBaseService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.service.RoleService;
import xyz.chener.zp.zpusermodule.service.UserBaseService;
import xyz.chener.zp.zpusermodule.service.UserExtendService;

import java.util.ArrayList;
import java.util.List;

/**
 * (OrgBase)表服务实现类
 *
 * @author makejava
 * @since 2023-02-16 09:43:14
 */
@Service
public class OrgBaseServiceImpl extends ServiceImpl<OrgBaseDao, OrgBase> implements OrgBaseService {

    private final UserBaseService userBaseService;
    private final UserExtendService userExtendService;
    private final RoleService roleService;

    public OrgBaseServiceImpl(UserBaseService userBaseService, UserExtendService userExtendService, RoleService roleService) {
        this.userBaseService = userBaseService;
        this.userExtendService = userExtendService;
        this.roleService = roleService;
    }

    @Override
    public List<OrgTreeDto> getOrgTree() {
        List<OrgTreeDto> orgTreeDtos = new ArrayList<>();
        getOrgTree(orgTreeDtos, null);
        return orgTreeDtos;
    }

    @Override
    public OrgInfoDto getOrgInfo(Integer id) {
        OrgInfoDto res = new OrgInfoDto();
        OrgBase org = lambdaQuery().eq(OrgBase::getId, id).one();
        if (org == null) {
            return null;
        }
        ObjectUtils.copyFields(org,res);
        UserBase userBase = userBaseService.lambdaQuery().select(UserBase::getUsername).eq(UserBase::getId, org.getMainUserId()).one();
        UserExtend userExtend = userExtendService.lambdaQuery().select(UserExtend::getNameCn).eq(UserExtend::getUserId, org.getMainUserId()).one();
        Role role = roleService.lambdaQuery().select(Role::getRoleName).eq(Role::getId, org.getRoleId()).one();
        res.setUsername(userBase.getUsername());
        res.setUserChName(userExtend.getNameCn());
        res.setRoleName(role.getRoleName());

        return res;
    }

    @Override
    public boolean saveOrUpdateOrg(OrgInfoDto orgInfoDto) {
        OrgBase orgBase = new OrgBase();
        ObjectUtils.copyFields(orgInfoDto,orgBase);
        return this.saveOrUpdate(orgBase);
    }


    private void getOrgTree(List<OrgTreeDto> list, Long parentId) {
        List<OrgBase> orgBases = null;
        if (parentId == null) {
            orgBases = this.lambdaQuery()
                    .select(OrgBase::getId,OrgBase::getOrgChSimpleName)
                    .isNull(OrgBase::getParentId).orderByAsc(OrgBase::getSortNum)
                    .list();

        } else {
            orgBases = this.lambdaQuery()
                    .select(OrgBase::getId,OrgBase::getOrgChSimpleName)
                    .eq(OrgBase::getParentId,parentId).orderByAsc(OrgBase::getSortNum)
                    .list();
        }
        orgBases.forEach(e->{
            OrgTreeDto orgTreeDto = new OrgTreeDto();
            orgTreeDto.coverToThis(e);
            getOrgTree(orgTreeDto.getChildren(), e.getId());
            list.add(orgTreeDto);
        });
    }
}

