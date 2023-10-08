package xyz.chener.zp.common.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import xyz.chener.zp.common.entity.LoginUserDetails;
import xyz.chener.zp.common.entity.SecurityVar;

import java.util.Collection;
import java.util.List;

public class SecurityUtils {

    public static Boolean hasAnyAuthority(String... authorities) {
        return hasAnyAuthority(false,authorities);
    }

    public static Boolean hasAnyAuthority(Boolean isUI,String... authorities) {
        Collection<? extends GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (auths instanceof List list){
            for (Object o : list) {
                for (String authority : authorities) {
                    if (o.toString().equals((isUI?SecurityVar.UI_PREFIX:SecurityVar.ROLE_PREFIX)+authority)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static LoginUserDetails currentUser(){
        try {
            return (LoginUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
        }catch (Exception e){
            return null;
        }
    }

}
