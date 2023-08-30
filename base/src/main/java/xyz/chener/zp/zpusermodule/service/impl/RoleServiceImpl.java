package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.dao.RoleDao;
import xyz.chener.zp.zpusermodule.entity.Role;
import xyz.chener.zp.zpusermodule.service.RoleService;

import java.util.List;

/**
 * (Role)表服务实现类
 *
 * @author makejava
 * @since 2023-01-11 15:22:52
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {

    @Override
    public Role saveOrUpdateRole(Long id, String name, List<String> permissionList) {
        StringBuilder sb = new StringBuilder();
        if (permissionList != null && permissionList.size() > 0)
        {
            permissionList.forEach(e->{
                sb.append(e).append(",");
            });
            sb.deleteCharAt(sb.length()-1);
        }

        Role role = new Role();
        role.setId(id);
        role.setRoleName(name);
        role.setPermissionEnNameList(sb.toString());
        saveOrUpdate(role);
        return role;
    }
}

