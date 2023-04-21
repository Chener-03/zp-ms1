package xyz.chener.zp.common.config.feign;

/**
 * @Author: chenzp
 * @Date: 2023/04/21/16:00
 * @Email: chen@chener.xyz
 */
public class RetryableUtils {

    /**
     * 触发openfeign重试的http状态码
     */
    private static final int[] RETRYABLE_CODES = {503, 408, 449, 429};


    public static boolean checkRetryable(int code){
        for (int retryableCode : RETRYABLE_CODES) {
            if (code == retryableCode) {
                return true;
            }
        }
        return false;
    }

}
