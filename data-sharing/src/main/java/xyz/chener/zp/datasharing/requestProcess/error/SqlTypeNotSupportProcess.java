package xyz.chener.zp.datasharing.requestProcess.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import xyz.chener.zp.common.entity.R;

import java.nio.charset.StandardCharsets;

/**
 * @Author: chenzp
 * @Date: 2023/04/13/17:01
 * @Email: chen@chener.xyz
 */
public class SqlTypeNotSupportProcess {

    public static void process(HttpServletRequest request, HttpServletResponse response,String sql){
        try(ServletOutputStream os = response.getOutputStream()) {
            response.setStatus(500);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            R<Object> r = R.Builder.getInstance().setCode(500)
                    .setMessage(String.format("不支持的SQL类型 %s.",sql))
                    .build();
            ObjectMapper om = new ObjectMapper();
            String s = om.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(r);
            os.write(s.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }catch (Exception ignored){ }
    }

}
