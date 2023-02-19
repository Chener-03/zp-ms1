package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.zpusermodule.entity.OrgBase;
import xyz.chener.zp.zpusermodule.entity.dto.OrgInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgTreeDto;

import java.util.List;

/**
 * (OrgBase)表服务接口
 *
 * @author makejava
 * @since 2023-02-16 09:43:14
 */
public interface OrgBaseService extends IService<OrgBase> {

    List<OrgTreeDto> getOrgTree();

    OrgInfoDto getOrgInfo(Integer id);

}

