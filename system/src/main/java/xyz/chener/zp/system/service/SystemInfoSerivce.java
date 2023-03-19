package xyz.chener.zp.system.service;

import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.system.entity.InstanceBaseHealth;
import xyz.chener.zp.system.entity.dto.InstanceDto;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/16:53
 * @Email: chen@chener.xyz
 */

public interface SystemInfoSerivce {


    List<InstanceDto> getInstances(InstanceDto dto);

    List<InstanceBaseHealth> getInstanceInfo(String url);

}
