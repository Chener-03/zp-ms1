package xyz.chener.zp.datasharing.service.impl;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Component;
import xyz.chener.zp.datasharing.entity.thirdparty.OrgBase;
import xyz.chener.zp.datasharing.entity.thirdparty.UserBase;
import xyz.chener.zp.datasharing.service.UserModuleService;

import java.util.Collections;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/01/13/08:50
 * @Email: chen@chener.xyz
 */


@Component("userModuleServiceFallback")
public class UserModuleServiceFallback implements UserModuleService {


    @Override
    public PageInfo<UserBase> getUserBaseInfo(UserBase userBase, Integer page, Integer size) {
        return new PageInfo<>(Collections.emptyList());
    }

    @Override
    public List<OrgBase> getUserOrgs(String username) {
        return Collections.emptyList();
    }
}
