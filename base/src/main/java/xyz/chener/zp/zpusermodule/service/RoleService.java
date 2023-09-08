package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.chener.zp.zpusermodule.entity.Role;

import java.util.List;

/**
 * (Role)表服务接口
 *
 * @author makejava
 * @since 2023-01-11 15:22:52
 */
public interface RoleService extends IService<Role> {
    Role saveOrUpdateRole(Long id, String name, List<String> permissionList);
}

