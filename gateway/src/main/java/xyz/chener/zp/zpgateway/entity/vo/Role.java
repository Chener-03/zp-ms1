package xyz.chener.zp.zpgateway.entity.vo;


public class Role   {


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

