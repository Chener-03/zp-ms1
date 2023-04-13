package xyz.chener.zp.datasharing.error.config;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

/**
 * @Author: chenzp
 * @Date: 2023/04/13/10:51
 * @Email: chen@chener.xyz
 */
public class BindDatasourceNotFount extends HttpErrorException {
    public BindDatasourceNotFount() {
        super(R.HttpCode.HTTP_ERR.get(), "绑定的数据源不存在或您没有权限访问");
    }
}
