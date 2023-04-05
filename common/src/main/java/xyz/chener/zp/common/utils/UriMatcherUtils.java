package xyz.chener.zp.common.utils;

import java.util.Arrays;

/**
 * @Author: chenzp
 * @Date: 2023/04/04/17:04
 * @Email: chen@chener.xyz
 */
public class UriMatcherUtils {

    /**
     * 判断给定的 URI 是否匹配指定的模式
     *
     * @param pattern 匹配模式
     * @param uri     需要匹配的 URI
     * @return 如果匹配成功则返回 true，否则返回 false
     */
    public static boolean match(String pattern, String uri) {
        String[] patternSegments = pattern.split("/");
        String[] uriSegments = uri.split("/");

        int patternIndex = 0;
        int uriIndex = 0;

        while (patternIndex < patternSegments.length && uriIndex < uriSegments.length) {
            String patternSegment = patternSegments[patternIndex];
            String uriSegment = uriSegments[uriIndex];

            if ("**".equals(patternSegment)) {
                // 多级通配符，可以匹配任意数量、任意深度的节点
                if (patternIndex == patternSegments.length - 1) {
                    // 如果多级通配符是模式的最后一段，直接匹配成功
                    return true;
                } else {
                    // 否则尝试从当前 URI 的剩余部分开始，递归匹配后续的模式段
                    for (int i = uriIndex; i < uriSegments.length; i++) {
                        if (match(join("/", Arrays.copyOfRange(patternSegments, patternIndex + 1, patternSegments.length)),
                                join("/", Arrays.copyOfRange(uriSegments, i, uriSegments.length)))) {
                            return true;
                        }
                    }
                    // 所有可能的递归匹配都失败了，匹配失败
                    return false;
                }
            } else if ("*".equals(patternSegment)) {
                // 单个通配符，可以匹配任意一个节点
                patternIndex++;
                uriIndex++;
            } else {
                // 普通节点，必须精确匹配
                if (!patternSegment.equals(uriSegment)) {
                    return false;
                }
                patternIndex++;
                uriIndex++;
            }
        }

        // 如果模式和 URI 都处理完了，说明匹配成功；否则匹配失败
        return patternIndex == patternSegments.length && uriIndex == uriSegments.length;
    }

    /**
     * 将多个字符串用指定的分隔符拼接起来
     *
     * @param separator 分隔符
     * @param segments  需要拼接的字符串
     * @return 拼接后的结果
     */
    private static String join(String separator, String[] segments) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                builder.append(separator);
            }
            builder.append(segments[i]);
        }
        return builder.toString();
    }


}
