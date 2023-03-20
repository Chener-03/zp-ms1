package xyz.chener.zp.sentinelAdapter.circuitbreak.error;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/03/20/17:19
 * @Email: chen@chener.xyz
 */
public class CircuitBreakError extends HttpErrorException {
    public CircuitBreakError() {
        super(R.HttpCode.HTTP_ERR.get(), "触发熔断但未配置熔断回调方法或回调出错");
    }
}
