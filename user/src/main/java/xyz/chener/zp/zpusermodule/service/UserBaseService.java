package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.OwnInformation;
import xyz.chener.zp.zpusermodule.entity.dto.ResetPasswordDto;
import xyz.chener.zp.zpusermodule.entity.dto.UserAllInfoDto;

/**
 * (UserBase)表服务接口
 *
 * @author makejava
 * @since 2023-01-11 15:23:15
 */
public interface UserBaseService extends IService<UserBase> {

    LoginResult processUsernameLogin(String username,String password);


    LoginResult processLogin(String username,String phone,String email,String password,String verification);

    boolean checkGoogleVerification(String code);

    OwnInformation getUserInformation(String username);

    PageInfo<UserAllInfoDto> getUserAllInfo(UserAllInfoDto userAllInfo , Integer page, Integer size,Boolean roleNotNull);

    UserBase addOrUpdateUser(UserBase userBase);

    ResetPasswordDto resetPassword(String username, String newPassword, String oldPassword);


}

