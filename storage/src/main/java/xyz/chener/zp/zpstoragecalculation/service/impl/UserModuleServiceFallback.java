package xyz.chener.zp.zpstoragecalculation.service.impl;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Component;
import xyz.chener.zp.zpstoragecalculation.entity.dto.UserAllInfoDto;
import xyz.chener.zp.zpstoragecalculation.service.UserModuleService;

import java.util.Collections;

/**
 * @Author: chenzp
 * @Date: 2023/01/13/08:50
 * @Email: chen@chener.xyz
 */


@Component("userModuleServiceFallback")
public class UserModuleServiceFallback implements UserModuleService {

    @Override
    public PageInfo<UserAllInfoDto> getUserAllInfo(String username, Boolean isLike) {
        PageInfo<UserAllInfoDto> info = new PageInfo<>(Collections.emptyList());
        return info;
    }
}
