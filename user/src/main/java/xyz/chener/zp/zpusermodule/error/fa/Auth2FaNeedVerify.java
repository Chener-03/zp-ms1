package xyz.chener.zp.zpusermodule.error.fa;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class Auth2FaNeedVerify extends HttpErrorException {
    public Auth2FaNeedVerify() {
        super(R.HttpCode.HTTP_2FA_NOT_AUTH.get(), R.ErrorMessage.HTTP_2FA_NOT_AUTH.get());
    }
}
