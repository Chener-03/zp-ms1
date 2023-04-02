package xyz.chener.zp.datasharing.connect.error;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class UnSupportType extends HttpErrorException {
    public UnSupportType() {
        super(R.HttpCode.HTTP_ERR.get(), "不支持的数据库类型");
    }
}
