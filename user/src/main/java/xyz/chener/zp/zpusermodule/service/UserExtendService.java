package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.chener.zp.zpusermodule.entity.UserExtend;

/**
 * (UserExtend)表服务接口
 *
 * @author makejava
 * @since 2023-01-19 15:19:57
 */
public interface UserExtendService extends IService<UserExtend> {


    UserExtend addOrUpdateUserExtend(UserExtend userExtend);

}

