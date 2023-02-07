package xyz.chener.zp.zpstoragecalculation.error;

import xyz.chener.zp.common.error.HttpErrorException;

public class FileServerDowmException extends HttpErrorException {
    public FileServerDowmException() {
        super(500,"The server where the file is located is down");
    }

    public FileServerDowmException(int httpCode, String httpErrorMessage) {
        super(httpCode, httpErrorMessage);
    }

    public FileServerDowmException(String httpErrorMessage) {
        super(httpErrorMessage);
    }
}
