package xyz.chener.zp.zpstoragecalculation.error;

import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;

public class FileNotExitsException extends HttpErrorException {
    public FileNotExitsException() {
        super(R.HttpCode.HTTP_PAGE_NOT_FOND.get(),"文件不存在");
    }

    public FileNotExitsException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public FileNotExitsException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
