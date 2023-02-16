package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.dao.PermissionDao;
import xyz.chener.zp.zpusermodule.entity.Permission;
import xyz.chener.zp.zpusermodule.entity.UiRouting;
import xyz.chener.zp.zpusermodule.service.PermissionService;
import xyz.chener.zp.zpusermodule.service.UiRoutingService;

import java.util.List;

/**
 * (Permission)表服务实现类
 *
 * @author makejava
 * @since 2023-01-11 15:22:36
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionDao, Permission> implements PermissionService {

    private final UiRoutingService uiRoutingService;

    public PermissionServiceImpl(UiRoutingService uiRoutingService) {
        this.uiRoutingService = uiRoutingService;
    }

    @Override
    public void flushUiPermission() {
        this.lambdaUpdate().likeRight(Permission::getPermissionEnName, "UI_").remove();
        List<Permission> list = uiRoutingService.lambdaQuery().orderByAsc(UiRouting::getId)
                .select(UiRouting::getMetaTitle, UiRouting::getName)
                .list().stream().map(e -> {
                    Permission permission = new Permission();
                    permission.setPermissionEnName("UI_" + e.getName());
                    permission.setPermissionChName(e.getMetaTitle());
                    return permission;
                }).toList();
        this.saveBatch(list);
    }
}

