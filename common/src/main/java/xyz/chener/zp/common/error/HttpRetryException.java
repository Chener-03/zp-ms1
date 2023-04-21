package xyz.chener.zp.common.error;

import xyz.chener.zp.common.entity.R;

/**
 * @Author: chenzp
 * @Date: 2023/04/21/15:35
 * @Email: chen@chener.xyz
 */
public class HttpRetryException extends HttpErrorException{
    public HttpRetryException() {
        super(R.HttpCode.HTTP_RETRY.get(), R.ErrorMessage.HTTP_RETRY.get());
    }
}
