package xyz.chener.zp.datasharing.service;


import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.datasharing.entity.thirdparty.UserBase;
import xyz.chener.zp.datasharing.service.impl.UserModuleServiceFallback;

import java.util.List;


@FeignClient(name = "zp-user-module",fallback = UserModuleServiceFallback.class)
public interface UserModuleService {

    @RequestMapping(value = "/api/web/getUserBaseInfo" , method = RequestMethod.GET)
    PageInfo<UserBase> getUserBaseInfo(@ModelAttribute @RequestParam("userBase") UserBase userBase
            , @RequestParam("page") Integer page
            , @RequestParam("size") Integer size);

}
