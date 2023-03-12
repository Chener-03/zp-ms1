package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.zpusermodule.entity.UserConfig;

/**
 * (UserConfig)表服务接口
 *
 * @author makejava
 * @since 2023-03-11 19:12:35
 */
public interface UserConfigService extends IService<UserConfig> {

    UserConfig getUserConfig(String username, FieldQuery fieldQuery);

    Boolean updateConcurrentUserLayoutConfig(String username, String layoutConfig);
}

