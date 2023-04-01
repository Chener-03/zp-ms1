package xyz.chener.zp.datasharing.error.datasource;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class SaveDatasourceError extends HttpErrorException {
    public SaveDatasourceError(String message) {
        super(R.HttpCode.HTTP_ERR.get(), "保存数据源失败:"+message);
    }

    public SaveDatasourceError() {
        super(R.HttpCode.HTTP_ERR.get(), "保存数据源失败");
    }
}
