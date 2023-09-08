package xyz.chener.zp.zpusermodule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * (Role)表实体类
 *
 * @author makejava
 * @since 2023-01-11 15:22:52
 */
@SuppressWarnings("serial")
public class Role extends Model<Role> {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    
    private String roleName;
    
    private String permissionEnNameList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPermissionEnNameList() {
        return permissionEnNameList;
    }

    public void setPermissionEnNameList(String permissionEnNameList) {
        this.permissionEnNameList = permissionEnNameList;
    }


    }

