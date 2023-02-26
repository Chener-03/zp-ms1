package xyz.chener.zp.zpusermodule.error.org;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class RootOrgNotDelete extends HttpErrorException {
    public RootOrgNotDelete() {
        super(R.HttpCode.HTTP_ERR.get(), "根组织不能删除");
    }
}
