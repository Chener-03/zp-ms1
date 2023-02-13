package xyz.chener.zp.zpusermodule.service;

import xyz.chener.zp.zpusermodule.entity.dto.MenuNameDto;
import xyz.chener.zp.zpusermodule.entity.dto.UiRoutingDto;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/13/16:09
 * @Email: chen@chener.xyz
 */
public interface MenuService {

    List<MenuNameDto> getAllMenuName();

}
