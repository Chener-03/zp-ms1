package xyz.chener.zp.zpgateway.utils;

/**
 * @Author: chenzp
 * @Date: 2023/04/04/17:04
 * @Email: chen@chener.xyz
 */
public class UriMatcherUtils {
    public static boolean match(String pattern, String uri) {
        if (pattern.equals(uri)) { // 完全匹配
            return true;
        }
        String[] patternSegments = pattern.split("/");
        String[] uriSegments = uri.split("/");
        int patternIndex = 0;
        int uriIndex = 0;
        while (patternIndex < patternSegments.length && uriIndex < uriSegments.length) {
            String patternSegment = patternSegments[patternIndex];
            String uriSegment = uriSegments[uriIndex];
            if (!matchSegment(patternSegment, uriSegment)) {
                return false;
            }
            patternIndex++;
            uriIndex++;
        }
        return patternIndex == patternSegments.length && uriIndex == uriSegments.length;
    }
    private static boolean matchSegment(String patternSegment, String uriSegment) {
        if (patternSegment.equals("**")) { // 多级通配符
            return true;
        } else if (patternSegment.equals("*")) { // 单个通配符
            return !uriSegment.isEmpty();
        } else {
            return patternSegment.equals(uriSegment);
        }
    }
}
