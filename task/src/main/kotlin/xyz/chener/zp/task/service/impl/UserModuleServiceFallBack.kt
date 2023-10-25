package xyz.chener.zp.task.service.impl

import com.github.pagehelper.PageInfo
import org.springframework.stereotype.Service
import xyz.chener.zp.task.entity.thirdparty.OrgBase
import xyz.chener.zp.task.entity.thirdparty.UserBase
import xyz.chener.zp.task.service.UserModuleService

@Service
class UserModuleServiceFallBack : UserModuleService {
    override fun getUserBaseInfo(userBase: UserBase?, page: Int?, size: Int?): PageInfo<UserBase?>? {
        return PageInfo<UserBase?>().also {
            it.total = 0
        }
    }

    override fun getUserOrgs(username: String?): List<OrgBase?>? {
        return emptyList()
    }

    override fun getUserBaseInfoByUserIds(userIds: List<Long?>?): List<UserBase> {
        return emptyList()
    }
}