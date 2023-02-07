package xyz.chener.zp.common.utils;

public class RoleStringUtils {

    public static String get(String ... auth)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("hasAnyRole(");
        for (String s : auth) {
            sb.append("'").append(s).append("'").append(",");
        }
        sb.deleteCharAt(sb.length());
        sb.append(")");
        return sb.toString();
    }

}
