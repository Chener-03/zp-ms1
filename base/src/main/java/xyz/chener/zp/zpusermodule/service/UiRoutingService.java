package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.chener.zp.zpusermodule.entity.UiRouting;
import xyz.chener.zp.zpusermodule.entity.dto.UiRoutingDto;

import java.util.List;

/**
 * (UiRouting)表服务接口
 *
 * @author makejava
 * @since 2023-01-19 22:55:16
 */
public interface UiRoutingService extends IService<UiRouting> {

    List<UiRoutingDto> getUserUiRouting(String username);

}

