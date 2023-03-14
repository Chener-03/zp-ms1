package xyz.chener.zp.common.config.antiShaking.error;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/03/14/10:53
 * @Email: chen@chener.xyz
 */
public class AntiShakingError extends HttpErrorException {
    public AntiShakingError() {
        super(R.HttpCode.HTTP_LIMIT.get(), "禁止短时间重复提交");
    }

}
