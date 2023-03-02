package xyz.chener.zp.zpusermodule.error.userinfo;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/03/02/10:27
 * @Email: chen@chener.xyz
 */
public class OnlyUpdateSelfError extends HttpErrorException {
    public OnlyUpdateSelfError() {
        super(R.HttpCode.HTTP_NO_ACCESS.get(), "只能修改自己的信息");
    }
}
