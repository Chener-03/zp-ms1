package xyz.chener.zp.zpusermodule.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.zpusermodule.entity.UiRouting;
import xyz.chener.zp.zpusermodule.entity.dto.MenuNameDto;
import xyz.chener.zp.zpusermodule.service.MenuService;
import xyz.chener.zp.zpusermodule.service.UiRoutingService;
import xyz.chener.zp.zpusermodule.service.impl.UiRoutingServiceImpl;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/13/15:38
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class MenuController {

    private final MenuService menuService;
    private final UiRoutingServiceImpl uiRoutingService;

    public MenuController(MenuService menuService, UiRoutingServiceImpl uiRoutingService) {
        this.menuService = menuService;
        this.uiRoutingService = uiRoutingService;
    }


    @GetMapping("/getAllMenusName")
    @PreAuthorize("hasAnyRole('menu_list_query')")
    public List<MenuNameDto> getAllMenusName() {
        return menuService.getAllMenuName();
    }


    @GetMapping("/getMenuInfo")
    @PreAuthorize("hasAnyRole('menu_list_query')")
    public UiRouting getMenuInfo(@RequestParam Integer id) {
        return uiRoutingService.lambdaQuery().eq(UiRouting::getId, id).one();
    }

    @PostMapping("/saveMenuInfo")
    @PreAuthorize("hasAnyRole('menu_list_query')")
    public UiRouting saveMenuInfo(@ModelAttribute UiRouting uiRouting) {
        return uiRoutingService.saveOrUpdate(uiRouting) ? uiRouting : null;
    }

    @PostMapping("/deleteMenuInfo")
    @PreAuthorize("hasAnyRole('menu_list_query')")
    public Boolean deleteMenuInfo(@RequestParam Integer id) {
        return uiRoutingService.removeById(id);
    }



    @GetMapping("/getAllParentMenus")
    @PreAuthorize("hasAnyRole('menu_list_query')")
    public List<UiRouting> getAllParentMenus() {
        return uiRoutingService.lambdaQuery().select(UiRouting::getId, UiRouting::getMetaTitle).list();
    }


}
