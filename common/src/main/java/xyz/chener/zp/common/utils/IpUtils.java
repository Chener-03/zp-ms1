package xyz.chener.zp.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.chener.zp.common.entity.CommonVar;

import java.util.Objects;

/**
 * @Author: chenzp
 * @Date: 2023/01/13/10:06
 * @Email: chen@chener.xyz
 */
public class IpUtils {

    public static String getRealIp()
    {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            return getRealIp(request);
        }catch (Exception exception)
        {
            return null;
        }
    }

    public static String getRealIp(HttpServletRequest request){
        try {
            String ip = request.getHeader(CommonVar.IP_HEAD);

            if (StringUtils.hasText(ip)) {
                return parseForwardedFor(ip);
            }

            ip = request.getHeader("X-Forwarded-For");

            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getHeader("X-Real-IP");
            }

            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip))
            {
                ip = request.getRemoteAddr();
            }

            return Objects.requireNonNullElseGet(parseForwardedFor(ip), request::getRemoteAddr);

        }catch (Exception err){
            return null;
        }
    }

    private static String parseForwardedFor(String f)
    {
        if (!StringUtils.hasText(f)) return null;
        return f.split(",")[0];
    }
}
