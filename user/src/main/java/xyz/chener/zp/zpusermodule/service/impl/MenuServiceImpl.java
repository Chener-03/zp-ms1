package xyz.chener.zp.zpusermodule.service.impl;

import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.entity.UiRouting;
import xyz.chener.zp.zpusermodule.entity.dto.MenuNameDto;
import xyz.chener.zp.zpusermodule.entity.dto.UiRoutingDto;
import xyz.chener.zp.zpusermodule.service.MenuService;
import xyz.chener.zp.zpusermodule.service.UiRoutingService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/13/16:10
 * @Email: chen@chener.xyz
 */

@Service
public class MenuServiceImpl implements MenuService {

    private final UiRoutingService uiRoutingService;

    public MenuServiceImpl(UiRoutingService uiRoutingService) {
        this.uiRoutingService = uiRoutingService;
    }


    @Override
    public List<MenuNameDto> getAllMenuName() {
        ArrayList<MenuNameDto> menuNameDtos = new ArrayList<>();
        getChildren(menuNameDtos,null);
        return menuNameDtos;
    }

    private void getChildren(List<MenuNameDto> menuNameDtos,Integer parentId){
        List<UiRouting> parent = null;
        if (parentId == null){
            parent = uiRoutingService.lambdaQuery()
                    .select(UiRouting::getId, UiRouting::getMetaTitle)
                    .isNull(UiRouting::getParentId).list();
        }else {
            parent = uiRoutingService.lambdaQuery()
                    .select(UiRouting::getId, UiRouting::getMetaTitle)
                    .eq(UiRouting::getParentId,parentId).list();
        }
        if (parent == null || parent.size() == 0){
            return;
        }
        parent.forEach(e->{
            MenuNameDto dto = new MenuNameDto();
            dto.setId(e.getId());
            dto.setLabel(e.getMetaTitle());
            getChildren(dto.getChildren(),e.getId());
            menuNameDtos.add(dto);
        });
    }


}
