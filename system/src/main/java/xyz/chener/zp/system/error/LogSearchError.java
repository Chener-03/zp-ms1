package xyz.chener.zp.system.error;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class LogSearchError extends HttpErrorException {
    public LogSearchError(String cause) {
        super(R.HttpCode.HTTP_ERR.get(), "日志查询失败");
        setHttpErrorMessage(cause);
    }

    public LogSearchError() {
        super(R.HttpCode.HTTP_ERR.get(), "日志查询失败");
    }
}
