package xyz.chener.zp.common.config.dynamicVerification.error;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/02/03/11:07
 * @Email: chen@chener.xyz
 */
public class DynamicVerificationError extends HttpErrorException {
    public DynamicVerificationError() {
        super(R.HttpCode.HTTP_NOT_ACCEPTABLE.get(),"参数动态校验失败,请检查校验方式");
    }

    public DynamicVerificationError(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public DynamicVerificationError(String httpErrorMessage) {
        super(R.HttpCode.HTTP_NOT_ACCEPTABLE.get(),"参数动态校验失败:"+httpErrorMessage);
    }
}
