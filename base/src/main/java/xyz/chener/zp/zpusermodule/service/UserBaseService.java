package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.constraints.NotNull;
import xyz.chener.zp.common.config.paramDecryption.annotation.RequestParamDecry;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.*;

import java.util.List;

/**
 * (UserBase)表服务接口
 *
 * @author makejava
 * @since 2023-01-11 15:23:15
 */
public interface UserBaseService extends IService<UserBase> {

    LoginResult processUsernameLogin(String username,String password,String systemEnum);


    LoginResult processLogin(String username,String phone,String email,String password,String verification);

    boolean checkGoogleVerification(String code);

    OwnInformation getUserInformation(String username);

    PageInfo<UserAllInfoDto> getUserAllInfo(UserAllInfoDto userAllInfo , Integer page, Integer size,Boolean roleNotNull);

    UserBase addOrUpdateUser(UserBase userBase);

    ResetPasswordDto resetPassword(String username, String newPassword, String oldPassword);



    List<String> getAllWsOnlineUsersName();

    List<OnlineUserInfo> getAllWsOnlineUsersData();


    LoginResult doLoginClient(String username,String password);


}

