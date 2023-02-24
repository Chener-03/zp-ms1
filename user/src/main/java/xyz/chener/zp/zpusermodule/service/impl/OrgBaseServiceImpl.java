package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import xyz.chener.zp.common.entity.SecurityVar;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.zpusermodule.dao.OrgBaseDao;
import xyz.chener.zp.zpusermodule.entity.*;
import xyz.chener.zp.zpusermodule.entity.dto.OrgInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgTreeDto;
import xyz.chener.zp.zpusermodule.service.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
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
    private OrgUserMapServiceImpl orgUserMapService;

    public OrgBaseServiceImpl(UserBaseService userBaseService, UserExtendService userExtendService, RoleService roleService) {
        this.userBaseService = userBaseService;
        this.userExtendService = userExtendService;
        this.roleService = roleService;
    }

    public OrgUserMapServiceImpl getOrgUserMapService() {
        return orgUserMapService;
    }

    @Autowired
    @Lazy
    public void setOrgUserMapService(OrgUserMapServiceImpl orgUserMapService) {
        this.orgUserMapService = orgUserMapService;
    }

    @Override
    public List<OrgTreeDto> getOrgTree(Long parentId) {
        List<OrgTreeDto> orgTreeDtos = new ArrayList<>();
        getOrgTree(orgTreeDtos, parentId);
        return orgTreeDtos;
    }

    @Override
    public Long getUserSelectOrgAuthorities(Collection<? extends GrantedAuthority> auths,String username) {
        if (auths instanceof List list){
            for (Object o : list) {
                GrantedAuthority gen = (GrantedAuthority) o;
                if (gen.getAuthority().equals(SecurityVar.ROLE_PREFIX+"org_list_query"))
                    return null;
            }
        }
        UserBase user = userBaseService.lambdaQuery().select(UserBase::getId).eq(UserBase::getUsername, username).one();
        OrgBase org = this.lambdaQuery().select(OrgBase::getId).eq(OrgBase::getMainUserId, user.getId()).one();
        if (org != null) {
            return org.getId();
        }
        OrgUserMap ou = orgUserMapService.lambdaQuery()
                .select(OrgUserMap::getOrgId).eq(OrgUserMap::getUserId, user.getId()).one();
        if (ou != null) {
            return ou.getOrgId();
        }
        return -1L;
    }

    @Override
    public OrgInfoDto getOrgInfo(Long id) {
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

