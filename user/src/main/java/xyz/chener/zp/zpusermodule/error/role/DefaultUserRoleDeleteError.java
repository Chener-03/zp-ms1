package xyz.chener.zp.zpusermodule.error.role;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/02/08/15:38
 * @Email: chen@chener.xyz
 */
public class DefaultUserRoleDeleteError extends HttpErrorException {

    public DefaultUserRoleDeleteError() {
        super(R.HttpCode.HTTP_ERR.get(), "默认用户角色状态不可改变");
    }

    public DefaultUserRoleDeleteError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public DefaultUserRoleDeleteError(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
