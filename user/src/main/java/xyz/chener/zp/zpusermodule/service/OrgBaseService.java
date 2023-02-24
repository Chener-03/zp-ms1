package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.zpusermodule.entity.OrgBase;
import xyz.chener.zp.zpusermodule.entity.dto.OrgInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgTreeDto;

import java.util.Collection;
import java.util.List;

/**
 * (OrgBase)表服务接口
 *
 * @author makejava
 * @since 2023-02-16 09:43:14
 */
public interface OrgBaseService extends IService<OrgBase> {

    List<OrgTreeDto> getOrgTree(Long parentId);

    Long getUserSelectOrgAuthorities(Collection<? extends GrantedAuthority> auths,String username);

    OrgInfoDto getOrgInfo(Long id);

    boolean saveOrUpdateOrg(OrgInfoDto orgInfoDto);

}

