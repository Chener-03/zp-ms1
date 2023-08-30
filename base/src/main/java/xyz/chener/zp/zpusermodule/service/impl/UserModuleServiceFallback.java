package xyz.chener.zp.zpusermodule.service.impl;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Component;
import xyz.chener.zp.zpusermodule.entity.Role;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.OnlineUserInfo;
import xyz.chener.zp.zpusermodule.service.UserModuleService;


import java.util.ArrayList;
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
    public List<String> getWsOnlineUsersName() {
        return new ArrayList<>();
    }

    @Override
    public List<OnlineUserInfo> getWsOnlineUsersDataForMs() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Boolean postQrCodeLoginGet(String sessionId) {
        return false;
    }

    @Override
    public Boolean postQrCodeLoginAuthorization(String sessionId, LoginResult result) {
        return false;
    }


}
