package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.chener.zp.zpusermodule.dao.UiRoutingDao;
import xyz.chener.zp.zpusermodule.entity.Role;
import xyz.chener.zp.zpusermodule.entity.UiRouting;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.UiRoutingDto;
import xyz.chener.zp.zpusermodule.service.UiRoutingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * (UiRouting)表服务实现类
 *
 * @author makejava
 * @since 2023-01-19 22:55:16
 */
@Service
public class UiRoutingServiceImpl extends ServiceImpl<UiRoutingDao, UiRouting> implements UiRoutingService {

    private final UserBaseServiceImpl userBaseService;
    private final RoleServiceImpl roleService;

    public UiRoutingServiceImpl(UserBaseServiceImpl userBaseService, RoleServiceImpl roleService) {
        this.userBaseService = userBaseService;
        this.roleService = roleService;
    }

    @Override
    public List<UiRoutingDto> getUserUiRouting(String username) {
        ArrayList<UiRoutingDto> res = new ArrayList<>();
        try {
            UserBase userBase = userBaseService.lambdaQuery().eq(UserBase::getUsername, username).one();
            if (Objects.nonNull(userBase))
            {
                Role role = roleService.lambdaQuery().eq(Role::getId, userBase.getRoleId()).one();
                String permissionEnNameList = role.getPermissionEnNameList();
                List<String> uiPerList = Arrays.stream(permissionEnNameList.split(","))
                        .filter(s -> StringUtils.hasText(s) && s.contains("UI_"))
                        .map(s -> s.substring(3)).toList();

                List<UiRouting> list = this.lambdaQuery().in(UiRouting::getName, uiPerList)
                        .isNull(UiRouting::getParentId).list();
                list.forEach(e->{
                    UiRoutingDto uiRoutingDto = new UiRoutingDto();
                    buildUiRoutingDto(e,uiRoutingDto,e.getId(),uiPerList);
                    res.add(uiRoutingDto);
                });
            }
        }catch (Exception ignored){}
        return res;
    }


    private void buildUiRoutingDto(UiRouting uiRouting,UiRoutingDto dto,int parentId,List<String> ui)
    {
        UiRoutingDto dt = uiRouting.coverToDto();
        dto.setPath(dt.getPath());
        dto.setName(dt.getName());
        dto.setComponent(dt.getComponent());
        dto.setRedirect(dt.getRedirect());
        dto.setMeta(dt.getMeta());

        List<UiRouting> list = this.lambdaQuery().in(UiRouting::getName, ui)
                .eq(UiRouting::getParentId,parentId).list();
        if (list.size()>0)
        {
            list.forEach(e->{
                UiRoutingDto subDto = new UiRoutingDto();
                buildUiRoutingDto(e,subDto,e.getId(),ui);
                dto.getChildren().add(subDto);
            });
        }
    }

}

