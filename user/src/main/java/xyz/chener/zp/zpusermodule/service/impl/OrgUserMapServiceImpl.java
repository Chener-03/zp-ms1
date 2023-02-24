package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import xyz.chener.zp.common.config.query.CustomFieldQuery;
import xyz.chener.zp.zpusermodule.dao.OrgUserMapDao;
import xyz.chener.zp.zpusermodule.entity.OrgBase;
import xyz.chener.zp.zpusermodule.entity.OrgUserMap;
import xyz.chener.zp.zpusermodule.entity.UserLoginEventRecord;
import xyz.chener.zp.zpusermodule.entity.dto.OrgExtendInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgUserDto;
import xyz.chener.zp.zpusermodule.service.OrgUserMapService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.service.UserLoginEventRecordService;

import java.util.Date;
import java.util.List;

/**
 * (OrgUserMap)表服务实现类
 *
 * @author makejava
 * @since 2023-02-21 11:55:56
 */
@Service
public class OrgUserMapServiceImpl extends ServiceImpl<OrgUserMapDao, OrgUserMap> implements OrgUserMapService {

    private final UserLoginEventRecordServiceImpl userLoginEventRecordService;
    private final OrgBaseServiceImpl orgBaseService;

    public OrgUserMapServiceImpl(UserLoginEventRecordServiceImpl userLoginEventRecordService, OrgBaseServiceImpl orgBaseService) {
        this.userLoginEventRecordService = userLoginEventRecordService;
        this.orgBaseService = orgBaseService;
    }

    @Override
    public OrgExtendInfoDto getOrgExtendInfo(Long id) {
        List<OrgUserMap> users = lambdaQuery().eq(OrgUserMap::getOrgId, id)
                .select(OrgUserMap::getUserId).list();
        OrgExtendInfoDto res = new OrgExtendInfoDto();
        res.setUserCount(users.size());
        res.setActiveUserCount(0);
        if (users.size()>0){
            Long count = userLoginEventRecordService.lambdaQuery()
                    .in(UserLoginEventRecord::getUserId, users)
                    .ge(UserLoginEventRecord::getTime, new Date())
                    .count();
            res.setActiveUserCount(count.intValue());
        }
        Long count1 = orgBaseService.lambdaQuery()
                .eq(OrgBase::getParentId, id).count();
        res.setSubOrgCount(count1.intValue());
        return res;
    }

    @Override
    public PageInfo<OrgUserDto> getOrgUsers(Long id, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<OrgUserDto> orgUserDtos = baseMapper.getOrgUsers(id);
        return new PageInfo<>(orgUserDtos);
    }
}

