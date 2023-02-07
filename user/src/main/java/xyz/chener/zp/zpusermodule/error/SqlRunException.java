package xyz.chener.zp.zpusermodule.error;

import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/01/11/15:56
 * @Email: chen@chener.xyz
 */
public class SqlRunException extends HttpErrorException {
    public SqlRunException() {
    }

    public SqlRunException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public SqlRunException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
