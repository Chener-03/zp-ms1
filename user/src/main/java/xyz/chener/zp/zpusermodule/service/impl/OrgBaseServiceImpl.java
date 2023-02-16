package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xyz.chener.zp.zpusermodule.dao.OrgBaseDao;
import xyz.chener.zp.zpusermodule.entity.OrgBase;
import xyz.chener.zp.zpusermodule.entity.dto.OrgTreeDto;
import xyz.chener.zp.zpusermodule.service.OrgBaseService;
import org.springframework.stereotype.Service;

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

    @Override
    public List<OrgTreeDto> getOrgTree() {
        List<OrgTreeDto> orgTreeDtos = new ArrayList<>();
        getOrgTree(orgTreeDtos, null);
        return orgTreeDtos;
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

