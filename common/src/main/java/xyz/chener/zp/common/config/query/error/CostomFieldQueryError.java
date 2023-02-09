package xyz.chener.zp.common.config.query.error;

/**
 * @Author: chenzp
 * @Date: 2023/02/09/11:22
 * @Email: chen@chener.xyz
 */
public class CostomFieldQueryError extends RuntimeException{
    public CostomFieldQueryError() {
    }

    public CostomFieldQueryError(String message) {
        super(message);
    }

    public CostomFieldQueryError(String message, Throwable cause) {
        super(message, cause);
    }

    public CostomFieldQueryError(Throwable cause) {
        super(cause);
    }

    public CostomFieldQueryError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
