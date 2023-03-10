package xyz.chener.zp.common.config.paramDecryption.error;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/03/10/15:15
 * @Email: chen@chener.xyz
 */
public class ParamBindError extends HttpErrorException {

    public ParamBindError() {
        super(R.HttpCode.BAD_REQUEST.get(), "参数绑定错误");
    }

    private Exception sourceException;


    public ParamBindError(String paramName, Exception sourceException) {
        super(R.HttpCode.BAD_REQUEST.get(), String.format("参数[%s]绑定错误", paramName));
        this.sourceException = sourceException;
    }

    public Exception getSourceException() {
        return sourceException;
    }
}
