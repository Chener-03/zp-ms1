package xyz.chener.zp.zpusermodule.error.org;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class OnlyOneRootOrg extends HttpErrorException {
    public OnlyOneRootOrg() {
        super(R.HttpCode.HTTP_ERR.get(),"只能有一个根组织");
    }
}
