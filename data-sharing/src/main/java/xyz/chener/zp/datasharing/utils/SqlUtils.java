package xyz.chener.zp.datasharing.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

/**
 * @Author: chenzp
 * @Date: 2023/04/13/10:58
 * @Email: chen@chener.xyz
 */
public class SqlUtils {


    /**
     * 获取SQL类型
     * @param sql
     * @return select update delete insert
     */
    public static String getSqlType(String sql) throws JSQLParserException {
        Statement parse = CCJSqlParserUtil.parse(sql);
        if (parse instanceof net.sf.jsqlparser.statement.select.Select)
            return "select";
        if (parse instanceof net.sf.jsqlparser.statement.update.Update)
            return "update";
        if (parse instanceof net.sf.jsqlparser.statement.delete.Delete)
            return "delete";
        if (parse instanceof net.sf.jsqlparser.statement.insert.Insert)
            return "insert";
        return null;
    }


    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String str) {
        StringBuilder camelCase = new StringBuilder();
        boolean capitalizeNext = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            if (ch == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    camelCase.append(Character.toUpperCase(ch));
                    capitalizeNext = false;
                } else {
                    camelCase.append(ch);
                }
            }
        }
        return camelCase.toString();
    }


    /**
     * 驼峰转下划线
     * @param str
     * @return
     */
    public static String toUnderscore(String str) {
        StringBuilder underscore = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                underscore.append("_");
                underscore.append(Character.toLowerCase(ch));
            } else {
                underscore.append(ch);
            }
        }

        return underscore.toString();
    }


}
