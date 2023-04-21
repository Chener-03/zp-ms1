package xyz.chener.zp.common.config.feign;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import xyz.chener.zp.common.error.HttpRetryException;
import xyz.chener.zp.common.utils.AssertUrils;

import java.util.Date;

/**
 * @Author: chenzp
 * @Date: 2023/04/21/15:57
 * @Email: chen@chener.xyz
 */
public class FeignRequestErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        AssertUrils.state(!RetryableUtils.checkRetryable(response.status())
                ,new RetryableException(response.status()
                        ,response.reason()
                        ,response.request().httpMethod()
                        ,new HttpRetryException()
                        ,new Date(),response.request()));
        return new Exception(String.format("[%s] %s", response.status(), response.reason()));
    }
}
