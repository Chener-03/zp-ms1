package xyz.chener.zp.datasharing.error.config;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/04/13/10:55
 * @Email: chen@chener.xyz
 */
public class SqlRunError extends HttpErrorException {
    public SqlRunError() {
        super(R.HttpCode.HTTP_ERR.get(), "SQL执行错误");
    }
    public SqlRunError(String message) {
        super(R.HttpCode.HTTP_ERR.get(), "SQL执行错误:"+message);
    }
}
