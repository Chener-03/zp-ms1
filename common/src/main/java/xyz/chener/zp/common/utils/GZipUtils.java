package xyz.chener.zp.common.utils;

import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author: chenzp
 * @Date: 2023/02/10/15:47
 * @Email: chen@chener.xyz
 */
public class GZipUtils {


    public static byte[] compressJson(String json) throws Exception
    {
        AssertUrils.state(StringUtils.hasText(json),new NullPointerException("json is null"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(os);
        gzip.write(json.getBytes(StandardCharsets.UTF_8));
        gzip.flush();
        gzip.close();
        return os.toByteArray();
    }

    public static String uncompressJson(byte[] data) throws Exception
    {
        AssertUrils.state(data.length > 0,new NullPointerException("json data is null"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream ungzip = new GZIPInputStream(new ByteArrayInputStream(data));
        StreamUtils.copy(ungzip,out);
        return out.toString(StandardCharsets.UTF_8);
    }

}
