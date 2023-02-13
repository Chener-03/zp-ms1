package xyz.chener.zp.zpusermodule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.dto.MenuNameDto;
import xyz.chener.zp.zpusermodule.service.MenuService;

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

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }


    @GetMapping("/getAllMenusName")
    @PreAuthorize("hasAnyRole('menu_list_query')")
    public List<MenuNameDto> getAllMenusName() {
        return menuService.getAllMenuName();
    }

}
