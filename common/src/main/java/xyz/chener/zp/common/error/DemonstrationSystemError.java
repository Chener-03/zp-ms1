package xyz.chener.zp.common.error;

import xyz.chener.zp.common.entity.R;

public class DemonstrationSystemError extends HttpErrorException{
    public DemonstrationSystemError() {
        super(R.HttpCode.HTTP_NO_ACCESS.get(), "演示系统，禁止修改");
    }
}
