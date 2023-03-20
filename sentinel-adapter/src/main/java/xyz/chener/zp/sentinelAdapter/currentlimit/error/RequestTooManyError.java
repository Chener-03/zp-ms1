package xyz.chener.zp.sentinelAdapter.currentlimit.error;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class RequestTooManyError extends HttpErrorException {
    public RequestTooManyError() {
        super(R.HttpCode.HTTP_LIMIT.get(), R.ErrorMessage.HTTP_LIMIT.get());
    }
}
