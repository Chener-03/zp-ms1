package xyz.chener.zp.zpgateway.service.impl;

import org.springframework.stereotype.Component;
import xyz.chener.zp.zpgateway.common.entity.vo.PageInfo;
import xyz.chener.zp.zpgateway.entity.vo.Role;
import xyz.chener.zp.zpgateway.entity.vo.UserBase;
import xyz.chener.zp.zpgateway.service.UserModuleService;

import java.util.Collections;

/**
 * @Author: chenzp
 * @Date: 2023/01/13/08:50
 * @Email: chen@chener.xyz
 */


@Component("userModuleServiceFallback")
public class UserModuleServiceFallback implements UserModuleService {

    @Override
    public PageInfo<UserBase> getUserBaseInfoByName(String username) {
        PageInfo<UserBase> res = new PageInfo<>();
        res.setList(Collections.EMPTY_LIST);
        return res;
    }

    @Override
    public PageInfo<Role> getUserRoleById(Long id) {
        PageInfo<Role> res = new PageInfo<>();
        res.setList(Collections.EMPTY_LIST);
        return res;
    }

    @Override
    public Boolean verify2Fa(String code, String username) {
        return false;
    }
}
