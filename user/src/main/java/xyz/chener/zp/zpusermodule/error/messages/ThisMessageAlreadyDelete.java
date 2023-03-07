package xyz.chener.zp.zpusermodule.error.messages;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/03/07/16:42
 * @Email: chen@chener.xyz
 */
public class ThisMessageAlreadyDelete extends HttpErrorException {
    public ThisMessageAlreadyDelete() {
        super(R.HttpCode.HTTP_ERR.get(), "该消息已被删除");
    }
}
