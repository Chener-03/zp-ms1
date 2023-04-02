package xyz.chener.zp.datasharing.requestProcess.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import xyz.chener.zp.common.entity.R;

import java.nio.charset.StandardCharsets;

public class RequestErrorProcess {

    public static void process(HttpServletRequest request, HttpServletResponse response,String message){
        try(ServletOutputStream os = response.getOutputStream()) {
            response.setStatus(500);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            R<Object> r = R.Builder.getInstance().setCode(500)
                    .setMessage(String.format("请求出错[%s],错误信息为:%s.", request.getRequestURI(),message))
                    .build();
            ObjectMapper om = new ObjectMapper();
            String s = om.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(r);
            os.write(s.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }catch (Exception ignored){ }
    }

}
