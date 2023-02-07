package xyz.chener.zp.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @Author: chenzp
 * @Date: 2023/02/03/17:28
 * @Email: chen@chener.xyz
 */
public class Md5Utiles {
    public static String getDataMd5(byte[] data){
        String md5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            md5 = sb.toString();
        }catch (Exception ignored) { }
        return md5;
    }

    public static String getStrMd5(String data){
        return getDataMd5(data.getBytes(StandardCharsets.UTF_8));
    }

}
