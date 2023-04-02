package xyz.chener.zp.datasharing.requestProcess.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import xyz.chener.zp.common.entity.R;

import java.nio.charset.StandardCharsets;

public class NoSuchRequestUidErrorProcess {
    public static void process(HttpServletRequest request, HttpServletResponse response){
        try(ServletOutputStream os = response.getOutputStream()) {
            response.setStatus(404);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            R<Object> r = R.Builder.getInstance().setCode(404)
                    .setMessage(String.format("当前请求ID[%s]未找到.", request.getRequestURI()))
                    .build();
            ObjectMapper om = new ObjectMapper();
            String s = om.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(r);
            os.write(s.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }catch (Exception ignored){ }
    }
}
