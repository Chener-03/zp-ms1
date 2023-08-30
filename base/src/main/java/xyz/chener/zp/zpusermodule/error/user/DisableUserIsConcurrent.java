package xyz.chener.zp.zpusermodule.error.user;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/01/29/16:51
 * @Email: chen@chener.xyz
 */
public class DisableUserIsConcurrent extends HttpErrorException {
    public DisableUserIsConcurrent() {
        super(R.HttpCode.HTTP_NOT_ACCEPTABLE.get(), "不能调整自己的禁用状态");
    }

    public DisableUserIsConcurrent(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public DisableUserIsConcurrent(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
