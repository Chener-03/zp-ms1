package xyz.chener.zp.zpusermodule.error.fa;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class Auth2FaVerifyFail extends HttpErrorException {
    public Auth2FaVerifyFail() {
        super(R.HttpCode.HTTP_2FA__AUTH_FAIL.get(), R.ErrorMessage.HTTP_2FA__AUTH_FAIL.get());
    }
}
