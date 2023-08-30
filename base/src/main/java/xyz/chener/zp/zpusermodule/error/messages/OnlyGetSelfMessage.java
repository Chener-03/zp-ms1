package xyz.chener.zp.zpusermodule.error.messages;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/03/07/16:40
 * @Email: chen@chener.xyz
 */
public class OnlyGetSelfMessage extends HttpErrorException {
    public OnlyGetSelfMessage() {
        super(R.HttpCode.HTTP_NOT_ACCEPTABLE.get(), "不是您的消息或消息已被您删除");
    }
}
