package xyz.chener.zp.datasharing.connect.error;

public class ConnectError extends RuntimeException{

    private Throwable throwable;

    public ConnectError(String message, Throwable error) {
        super(message);
        this.throwable = error;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
