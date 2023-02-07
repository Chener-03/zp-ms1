package xyz.chener.zp.zpusermodule.service;

/**
 * @Author: chenzp
 * @Date: 2023/02/06/14:32
 * @Email: chen@chener.xyz
 */


public interface GoogleRecapthaService {

    String secret = "6LfSuiUiAAAAAHfI_Dk5EDJ94INqPVnw-1AMbqJ9";

    Float score = 0.7f;

    boolean check(String response);

}
