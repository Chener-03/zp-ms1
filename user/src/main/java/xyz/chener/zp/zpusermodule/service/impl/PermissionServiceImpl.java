package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.dao.PermissionDao;
import xyz.chener.zp.zpusermodule.entity.Permission;
import xyz.chener.zp.zpusermodule.service.PermissionService;

import java.util.List;

/**
 * (Permission)表服务实现类
 *
 * @author makejava
 * @since 2023-01-11 15:22:36
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionDao, Permission> implements PermissionService {

    @Override
    public void flushUiPermission() {
        List<Permission> ui = this.lambdaQuery().likeRight(Permission::getPermissionEnName, "UI_").list();
        System.out.println();
    }
}

