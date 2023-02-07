package xyz.chener.zp.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import xyz.chener.zp.common.entity.R;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;



public class AccessDeniedProcess implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        try (OutputStream os = response.getOutputStream()){
            response.setStatus(R.HttpCode.HTTP_OK.get());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper om = new ObjectMapper();
            String s = om.writeValueAsString(R.Builder.getInstance()
                    .setCode(R.HttpCode.HTTP_NO_ACCESS.get())
                    .setMessage(R.ErrorMessage.HTTP_NO_ACCESS.get()).build());
            StreamUtils.copy(s.getBytes(StandardCharsets.UTF_8),os);
        }
    }
}
