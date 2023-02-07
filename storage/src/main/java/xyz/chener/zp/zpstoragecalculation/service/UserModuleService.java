package xyz.chener.zp.zpstoragecalculation.service;


import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.zpstoragecalculation.entity.dto.UserAllInfoDto;
import xyz.chener.zp.zpstoragecalculation.service.impl.UserModuleServiceFallback;

@FeignClient(name = "zp-user-module",fallback = UserModuleServiceFallback.class)
public interface UserModuleService {

    @RequestMapping("/api/web/getUserAllInfo")
    @PreAuthorize("hasAnyRole('microservice_call','user_user_list')")
    PageInfo<UserAllInfoDto> getUserAllInfo(@RequestParam("username") String username
            , @RequestParam("isLike") Boolean isLike);

}
