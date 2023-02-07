package xyz.chener.zp.zpstoragecalculation.error;

import xyz.chener.zp.common.error.HttpErrorException;

public class FileTransferException extends HttpErrorException {
    public FileTransferException() {
        super(500,"网络波动,文件传输异常");
    }

    public FileTransferException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public FileTransferException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
