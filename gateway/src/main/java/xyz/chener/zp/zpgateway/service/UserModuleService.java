package xyz.chener.zp.zpgateway.service;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.sentinelAdapter.circuitbreak.annotation.CircuitBreakResourceFeign;
import xyz.chener.zp.zpgateway.common.entity.vo.PageInfo;
import xyz.chener.zp.zpgateway.entity.vo.Role;
import xyz.chener.zp.zpgateway.entity.vo.UserBase;
import xyz.chener.zp.zpgateway.service.impl.UserModuleServiceFallback;

@FeignClient(name = "zp-user-module",fallback = UserModuleServiceFallback.class)
@CircuitBreakResourceFeign
public interface UserModuleService {

    @RequestMapping(value = "/api/web/getUserBaseInfo",method = RequestMethod.GET)
    PageInfo<UserBase> getUserBaseInfoByName(@RequestParam("username") String username);

    @RequestMapping(value = "/api/web/getUserRole",method = RequestMethod.GET)
    PageInfo<Role> getUserRoleById(@RequestParam("id") Long id);

    @RequestMapping(value = "/api/web/2fa/verify",method = RequestMethod.POST)
    Boolean verify2Fa(@RequestParam(value = "code",defaultValue = "") String code ,@RequestParam("username") String username);

}
