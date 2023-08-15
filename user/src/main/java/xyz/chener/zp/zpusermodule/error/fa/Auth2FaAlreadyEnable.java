package xyz.chener.zp.zpusermodule.error.fa;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class Auth2FaAlreadyEnable extends HttpErrorException {
    public Auth2FaAlreadyEnable() {
        super(R.HttpCode.HTTP_ERR.get(), "2FA已经启用");
    }
}
