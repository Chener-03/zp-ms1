package xyz.chener.zp.zpusermodule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * (Permission)表实体类
 *
 * @author makejava
 * @since 2023-01-11 15:22:35
 */
@SuppressWarnings("serial")
public class Permission extends Model<Permission> {


    @TableId(type = IdType.INPUT)
    private String permissionEnName;
    
    private String permissionChName;


    public String getPermissionEnName() {
        return permissionEnName;
    }

    public void setPermissionEnName(String permissionEnName) {
        this.permissionEnName = permissionEnName;
    }

    public String getPermissionChName() {
        return permissionChName;
    }

    public void setPermissionChName(String permissionChName) {
        this.permissionChName = permissionChName;
    }


    }

