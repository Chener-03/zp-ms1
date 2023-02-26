package xyz.chener.zp.zpusermodule.error.org;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class OrgNotFoundError extends HttpErrorException {
    public OrgNotFoundError() {
        super(R.HttpCode.HTTP_ERR.get(), "组织不存在");
    }
}
