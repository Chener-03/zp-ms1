package xyz.chener.zp.zpusermodule.error.role;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/02/08/11:45
 * @Email: chen@chener.xyz
 */
public class DefaultRoleDeleteError extends HttpErrorException {
    public DefaultRoleDeleteError() {
        super(R.HttpCode.HTTP_ERR.get(),"默认角色不可操作");
    }

    public DefaultRoleDeleteError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public DefaultRoleDeleteError(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
