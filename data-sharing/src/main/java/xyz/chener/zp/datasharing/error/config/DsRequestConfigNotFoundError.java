package xyz.chener.zp.datasharing.error.config;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/04/18/16:10
 * @Email: chen@chener.xyz
 */
public class DsRequestConfigNotFoundError extends HttpErrorException {
    public DsRequestConfigNotFoundError() {
        super(R.HttpCode.HTTP_ERR.get(), "请求的配置不存在");
    }
}
