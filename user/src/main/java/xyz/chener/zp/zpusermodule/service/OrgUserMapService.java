package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.zpusermodule.entity.OrgUserMap;
import xyz.chener.zp.zpusermodule.entity.dto.OrgExtendInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgUserDto;

import java.util.List;

/**
 * (OrgUserMap)表服务接口
 *
 * @author makejava
 * @since 2023-02-21 11:55:56
 */
public interface OrgUserMapService extends IService<OrgUserMap> {

    OrgExtendInfoDto getOrgExtendInfo(Long id);

    PageInfo<OrgUserDto> getOrgUsers(Long id, Integer page, Integer size);

}

