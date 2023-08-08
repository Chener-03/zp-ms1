package xyz.chener.zp.common.config.auth2fa.triggetsrategy;

import xyz.chener.zp.common.entity.LoginUserDetails;

public interface FaTrigetStrategyInterface {

    default Boolean needCheck(LoginUserDetails userDetails){
        return false;
    };

}
