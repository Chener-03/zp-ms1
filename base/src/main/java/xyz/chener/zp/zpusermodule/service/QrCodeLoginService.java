package xyz.chener.zp.zpusermodule.service;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.zpusermodule.entity.dto.LoginResult;
import xyz.chener.zp.zpusermodule.entity.dto.QrCodeLoginRespDto;

/**
 * @Author: chenzp
 * @Date: 2023/06/15/11:50
 * @Email: chen@chener.xyz
 */
public interface QrCodeLoginService {

    int EXPIRE_TIME = 60*3;

    String QRCODE_LOGIN_CACHE_KEY = "QRCODE_LOGIN_CACHE_KEY::";

    boolean putQrCodeLogin(String uuid,String sessionId,String ip,String os);

    QrCodeLoginRespDto qrCodeGet(String uuid);

    boolean postQrCodeLoginGet(String sessionId);

    Boolean qrCodeAuthorization(String uuid,String username);

    boolean postQrCodeLoginAuthorization(String sessionId,LoginResult result);

}
