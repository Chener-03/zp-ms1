package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.chener.zp.zpusermodule.entity.User2fa;
import xyz.chener.zp.zpusermodule.entity.dto.Auth2FaMessageDto;

/**
 * (User2fa)表服务接口
 *
 * @author makejava
 * @since 2023-07-25 20:21:47
 */
public interface User2faService extends IService<User2fa> {

    Auth2FaMessageDto enable2Fa(String username);

}

