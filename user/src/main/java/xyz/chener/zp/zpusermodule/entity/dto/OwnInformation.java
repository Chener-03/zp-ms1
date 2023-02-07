package xyz.chener.zp.zpusermodule.entity.dto;

import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.UserExtend;

import java.util.ArrayList;
import java.util.List;

public class OwnInformation {
    private UserBase userBase;

    private UserExtend userExtend;

    private final List<String> roleList = new ArrayList<>();

    public List<String> getRoleList() {
        return roleList;
    }

    public UserExtend getUserExtend() {
        return userExtend;
    }

    public void setUserExtend(UserExtend userExtend) {
        this.userExtend = userExtend;
    }

    public UserBase getUserBase() {
        return userBase;
    }

    public void setUserBase(UserBase userBase) {
        this.userBase = userBase;
    }

}
