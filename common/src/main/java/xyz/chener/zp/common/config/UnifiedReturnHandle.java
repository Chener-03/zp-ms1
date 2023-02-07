package xyz.chener.zp.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import xyz.chener.zp.common.entity.R;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;

public class UnifiedReturnHandle  implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        Annotation[] methodAnnotations = returnType.getMethodAnnotations();
        Annotation[] annotations = returnType.getDeclaringClass().getAnnotations();
        return containAnn(methodAnnotations, UnifiedReturn.class.getName())
                || containAnn(annotations, UnifiedReturn.class.getName());
    }

    private boolean containAnn(Annotation[] anns,String annName)
    {
        for (Annotation ann : anns) {
            if (ann.annotationType().getName().equals(annName))
                return true;
        }
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse resp = webRequest.getNativeResponse(HttpServletResponse.class);
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setStatus(R.HttpCode.HTTP_OK.get());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String json = "";
        ObjectMapper om = new ObjectMapper();
        if (returnValue instanceof R)
        {
            json = om.writeValueAsString(returnValue);
        }else
        {
            json = om.writeValueAsString(R.Builder.getInstance().setCode(R.HttpCode.HTTP_OK.get())
                    .setObj(returnValue).build());
        }
        try (ServletOutputStream os = resp.getOutputStream()){
            StreamUtils.copy(json.getBytes(StandardCharsets.UTF_8),os);
        }
    }
}