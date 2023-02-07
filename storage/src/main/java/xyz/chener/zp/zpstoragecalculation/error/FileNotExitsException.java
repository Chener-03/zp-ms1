package xyz.chener.zp.zpstoragecalculation.error;

import xyz.chener.zp.common.error.HttpErrorException;

public class FileNotExitsException extends HttpErrorException {
    public FileNotExitsException() {
        super(500,"文件不存在");
    }

    public FileNotExitsException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public FileNotExitsException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
