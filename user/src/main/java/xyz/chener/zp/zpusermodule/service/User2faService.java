package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.common.config.paramDecryption.annotation.ModelAttributeDecry;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
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

    Auth2FaMessageDto confirmEnable2Fa(Auth2FaMessageDto code,String username);

    Integer verify2Fa(String code,String username,boolean required,boolean containsHeader);

    Boolean disable2Fa(String code,String username);


}

