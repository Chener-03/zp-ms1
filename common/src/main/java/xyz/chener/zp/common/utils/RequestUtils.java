package xyz.chener.zp.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @Author: chenzp
 * @Date: 2023/01/16/11:22
 * @Email: chen@chener.xyz
 */
public class RequestUtils {

    public static HttpServletRequest getConcurrentRequest()
    {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra instanceof ServletRequestAttributes sra)
        {
            return sra.getRequest();
        }
        return null;
    }


    public static HttpServletResponse getConcurrentResponse()
    {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra instanceof ServletRequestAttributes sra)
        {
            return sra.getResponse();
        }
        return null;
    }


    public static String getConcurrentHeader(String key)
    {
        HttpServletRequest concurrentRequest = getConcurrentRequest();
        if (Objects.nonNull(concurrentRequest))
            return concurrentRequest.getHeader(key);
        return null;
    }


    public static String getConcurrentIp()
    {
        return IpUtils.getRealIp();
    }


    public static String getConcurrentOs()
    {
        String userAgent = getConcurrentHeader(HttpHeaders.USER_AGENT);
        String os = null;
        String browser = null;
        if (StringUtils.hasText(userAgent))
        {
            // system
            {
                if (userAgent.contains("AndroidApp") || userAgent.contains("IosApp")){
                    int startIndex = userAgent.indexOf('(');
                    int endIndex = userAgent.indexOf(')');
                    if (startIndex != -1 && endIndex != -1){
                        os = userAgent.substring(startIndex + 1, endIndex);
                    }
                } else if (userAgent.toLowerCase().contains("windows"))
                {
                    os = "Windows";
                }else if (userAgent.toLowerCase().contains("mac"))
                {
                    os = "Mac";
                }else if (userAgent.toLowerCase().contains("x11"))
                {
                    os = "Unix";
                }else if (userAgent.toLowerCase().contains("android"))
                {
                    os = "Android";
                }else if (userAgent.toLowerCase().contains("iphone"))
                {
                    os = "Iphone";
                }
            }

            // brower
            {
                String user = userAgent.toLowerCase();
                if (userAgent.contains("AndroidApp") || userAgent.contains("IosApp")){
                    browser="";
                }else if (user.contains("edg"))
                {
                    browser=(userAgent.substring(userAgent.indexOf("Edg")).split(" ")[0]).replace("/", "-");
                } else if (user.contains("msie"))
                {
                    String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
                    browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
                } else if (user.contains("safari") && user.contains("version"))
                {
                    browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]
                            + "-" +(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
                } else if ( user.contains("opr") || user.contains("opera"))
                {
                    if(user.contains("opera")){
                        browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]
                                +"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
                    }else if(user.contains("opr")){
                        browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-"))
                                .replace("OPR", "Opera");
                    }

                } else if (user.contains("chrome"))
                {
                    browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
                } else if ((user.contains("mozilla/7.0")) || (user.contains("netscape6"))  ||
                        (user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78")) ||
                        (user.contains("mozilla/4.08")) || (user.contains("mozilla/3")) )
                {
                    browser = "Netscape-?";
                } else if (user.contains("firefox"))
                {
                    browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
                } else if(user.contains("rv"))
                {
                    String IEVersion = (userAgent.substring(userAgent.indexOf("rv")).split(" ")[0]).replace("rv:", "-");
                    browser="IE" + IEVersion.substring(0,IEVersion.length() - 1);
                }
            }
        }
        String res = os == null && browser == null ? "Unknown" : String.format("%s-%s",os,browser);
        return res.charAt(res.length()-1) == '-' ? res.substring(0,res.length()-1):res;
    }

}
