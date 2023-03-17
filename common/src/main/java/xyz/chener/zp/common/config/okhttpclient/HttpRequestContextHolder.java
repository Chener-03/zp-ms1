package xyz.chener.zp.common.config.okhttpclient;

/**
 * @Author: chenzp
 * @Date: 2023/03/17/15:55
 * @Email: chen@chener.xyz
 */
public class HttpRequestContextHolder {
    private static final ThreadLocal<String> baseUrlLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> extendUrlLocal = new ThreadLocal<>();

    private static final ThreadLocal<String> fullUrlLocal = new ThreadLocal<>();

    public static String setNextBaseUrl(String url)
    {
        baseUrlLocal.set(url);
        return url;
    }


    public static String setNextExtendUrl(String url)
    {
        extendUrlLocal.set(url);
        return url;
    }


    public static String setNextFullUrl(String url)
    {
        fullUrlLocal.set(url);
        return url;
    }



    public static void clearNextInstance()
    {
        baseUrlLocal.remove();
        extendUrlLocal.remove();
        fullUrlLocal.remove();
    }

    public static UrlInfo getNextUrlInfo()
    {
        UrlInfo urlInfo = new UrlInfo();
        urlInfo.baseUrl = baseUrlLocal.get();
        urlInfo.extendUrl = extendUrlLocal.get();
        urlInfo.fullUrl = fullUrlLocal.get();
        clearNextInstance();
        return urlInfo;
    }


    public static class UrlInfo{
        public String baseUrl;
        public String extendUrl;
        public String fullUrl;
    }

}
