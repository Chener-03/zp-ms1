package xyz.chener.zp.zpusermodule.service;


import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.OnlineUserInfo;
import xyz.chener.zp.zpusermodule.service.impl.UserModuleServiceFallback;

import java.util.List;


@FeignClient(name = "zp-user-module",fallback = UserModuleServiceFallback.class)
public interface UserModuleService {

    @RequestMapping(value = "/api/web/getWsOnlineUsersForMs",method = RequestMethod.GET)
    List<String> getWsOnlineUsersName();

    @RequestMapping(value = "/api/web/getWsOnlineUsersDataForMs",method = RequestMethod.GET)
    List<OnlineUserInfo> getWsOnlineUsersDataForMs();

    @RequestMapping(value = "/api/client/user/postQrCodeLoginGet",method = RequestMethod.POST)
    Boolean postQrCodeLoginGet(@RequestParam("sessionId") String sessionId);

    @RequestMapping(value = "/api/client/user/postQrCodeLoginAuthorization",method = RequestMethod.POST )
    Boolean postQrCodeLoginAuthorization(@RequestParam("sessionId") String sessionId
            ,@RequestBody LoginResult result);


}
