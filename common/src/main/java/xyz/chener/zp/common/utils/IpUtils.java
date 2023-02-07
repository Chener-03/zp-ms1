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
            String ip_HEAD = request.getHeader(CommonVar.IP_HEAD);
            if (StringUtils.hasText(ip_HEAD)) return ip_HEAD;
            String forwarded = request.getHeader("x-forwarded-for");
            String ip = parseForwardedFor(forwarded);
            return Objects.requireNonNullElseGet(ip, request::getRemoteAddr);
        }catch (Exception exception)
        {
            return null;
        }
    }

    private static String parseForwardedFor(String f)
    {
        if (!StringUtils.hasText(f)) return null;
        int i = f.indexOf(",");
        if (i < 0)
            return f;
        else return f.substring(0,i);
    }
}
