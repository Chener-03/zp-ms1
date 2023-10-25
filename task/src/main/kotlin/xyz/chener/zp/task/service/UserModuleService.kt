package xyz.chener.zp.task.service

import com.baomidou.mybatisplus.core.toolkit.support.SFunction
import com.github.pagehelper.PageInfo
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import xyz.chener.zp.task.entity.thirdparty.OrgBase
import xyz.chener.zp.task.entity.thirdparty.UserBase
import xyz.chener.zp.task.service.impl.UserModuleServiceFallBack


@FeignClient( name = "zp-base-module", fallback = UserModuleServiceFallBack::class)
interface UserModuleService {

    @RequestMapping(value = ["/api/web/getUserBaseInfo"], method = [RequestMethod.GET])
    fun getUserBaseInfo(
        @ModelAttribute @RequestParam("userBase") userBase: UserBase?,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): PageInfo<UserBase?>?

    @RequestMapping(value = ["/api/web/getUserOrgsByUsername"], method = [RequestMethod.GET])
    fun getUserOrgs(@RequestParam(value = "username") username: String?): List<OrgBase?>?

    @RequestMapping(value = ["/api/web/getUserBaseInfoByUserIds"])
    fun getUserBaseInfoByUserIds(@RequestBody userIds: List<Long?>?): List<UserBase>

}