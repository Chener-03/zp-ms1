package xyz.chener.zp.common.config.unifiedReturn;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import xyz.chener.zp.common.config.ctx.ApplicationContextHolder;
import xyz.chener.zp.common.config.security.AccessDeniedProcess;
import xyz.chener.zp.common.config.unifiedReturn.annotation.DispatchException;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;
import xyz.chener.zp.common.utils.LoggerUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


@RestControllerAdvice
@Slf4j
public class UnifiedErrorReturn {

    @DispatchException(HttpErrorException.class)
    public R<String> httpErrorProcess(HttpErrorException exception)
    {
        log.error(exception.getHttpErrorMessage());
        return R.Builder.<String>getInstance()
                .setCode(exception.getHttpCode())
                .setMessage(exception.getHttpErrorMessage())
                .setObj(TraceContext.traceId())
                .build();
    }

    @DispatchException(ConstraintViolationException.class)
    public R<String> jsr303ViolationErrorProcess(ConstraintViolationException exception)
    {
        log.error(exception.getMessage());
        exception.printStackTrace();
        StringBuilder sb = new StringBuilder();
        exception.getConstraintViolations().forEach(v->{
            Optional.ofNullable(v.getInvalidValue()).ifPresent(o -> sb.append("值:").append(o.toString()));
            sb.append(" 提示:").append(v.getMessage()).append(";");
        });
        return R.Builder.<String>getInstance()
                .setCode(R.HttpCode.BAD_REQUEST.get())
                .setMessage(String.format("%s [%s]",R.ErrorMessage.BAD_REQUEST.get(),sb.toString()))
                .setObj(TraceContext.traceId())
                .build();
    }


    @DispatchException(value = NoHandlerFoundException.class,otherParams = {HttpServletRequest.class,HttpServletResponse.class})
    public R<String> pageNotFountProcess(NoHandlerFoundException exception
            , HttpServletRequest request, HttpServletResponse response)
    {
        exception.printStackTrace();
        log.error(exception.getMessage());
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        if (Objects.nonNull(accept) && accept.contains(MediaType.TEXT_HTML_VALUE))
        {
            try (ServletOutputStream os = response.getOutputStream()){
                response.setContentType(MediaType.TEXT_HTML_VALUE);
                response.setStatus(R.HttpCode.HTTP_PAGE_NOT_FOND.get());
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                String body = String.format("<h3>page not found [%s]</h3>",exception.getRequestURL());
                StreamUtils.copy(body.getBytes(StandardCharsets.UTF_8),os);
                return null;
            }catch (Exception ignored){ }
        }

        return R.Builder.<String>getInstance()
                .setCode(R.HttpCode.HTTP_PAGE_NOT_FOND.get())
                .setMessage(String.format("%s [%s]"
                        ,R.ErrorMessage.HTTP_PAGE_NOT_FOND.get()
                        ,exception.getRequestURL()))
                .setObj(TraceContext.traceId())
                .build();
    }


    @DispatchException(ServletRequestBindingException.class)
    public R<String> paramErrorProcess(ServletRequestBindingException exception)
    {
        exception.printStackTrace();
        log.error(exception.getMessage());
        return R.Builder.<String>getInstance()
                .setCode(R.HttpCode.BAD_REQUEST.get())
                .setMessage(String.format("%s"
                        ,R.ErrorMessage.BAD_REQUEST.get()))
                .setObj(TraceContext.traceId())
                .build();
    }


    @DispatchException(HttpRequestMethodNotSupportedException.class)
    public R<String> methodNotAllowErrorProcess(HttpRequestMethodNotSupportedException exception)
    {
        exception.printStackTrace();
        log.error(exception.getMessage());
        return R.Builder.<String>getInstance()
                .setCode(R.HttpCode.METHOD_ERROR.get())
                .setMessage(String.format("%s [%s]"
                        ,R.ErrorMessage.METHOD_ERROR.get(),exception.getMethod()))
                .setObj(TraceContext.traceId())
                .build();
    }


    @DispatchException(BindException.class)
    public R<String> paramBindException (BindException exception)
    {
        exception.printStackTrace();
        log.error(exception.getMessage());
        StringBuilder sb = new StringBuilder();
        exception.getFieldErrors().forEach(e->{
            sb.append("值:").append(e.getRejectedValue()).append(" 提示:").append(e.getDefaultMessage()).append(";");
        });
        return R.Builder.<String>getInstance()
                .setCode(R.HttpCode.HTTP_NOT_ACCEPTABLE.get())
                .setMessage(String.format("%s [%s]"
                        ,R.ErrorMessage.HTTP_NOT_ACCEPTABLE.get(),sb.toString()))
                .setObj(TraceContext.traceId())
                .build();
    }


    @DispatchException(value = AccessDeniedException.class,otherParams = {HttpServletRequest.class,HttpServletResponse.class})
    public R<String> accessDeniedException(AccessDeniedException exception, HttpServletRequest request, HttpServletResponse response)
    {
        try {
            AccessDeniedProcess accessDeniedProcess = ApplicationContextHolder
                    .getApplicationContext()
                    .getBean(AccessDeniedProcess.class);
            accessDeniedProcess.handle(request,response,exception);
        } catch (Exception ignored) { }
        return new R<>();
    }

    @DispatchException(MethodArgumentTypeMismatchException.class)
    public R<String> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception)
    {
        LoggerUtils.logErrorStackTrace(exception,log);
        return R.Builder.<String>getInstance()
                .setCode(R.HttpCode.BAD_REQUEST.get())
                .setMessage(String.format("%s [%s]"
                        ,R.ErrorMessage.BAD_REQUEST.get(),exception.getMessage()))
                .setObj(TraceContext.traceId())
                .build();
    }


    @ExceptionHandler(Exception.class)
    public R<String> exceptionDispatch(Exception exception, HttpServletRequest request, HttpServletResponse response)
    {
        AtomicReference<R<String>> res = new AtomicReference<>(null);
        Arrays.stream(this.getClass().getDeclaredMethods()).filter(method -> {
            DispatchException de = method.getAnnotation(DispatchException.class);
            if (Objects.nonNull(de))
            {
                for (Class<? extends Throwable> aClass : de.value()) {
                    if (aClass.isAssignableFrom(exception.getClass()))
                        return true;
                }
            }
            return false;
        }).forEach(m->{
            boolean b = m.canAccess(this);
            m.setAccessible(true);
            try {
                Class<?>[] otherParams = m.getAnnotation(DispatchException.class).otherParams();
                ArrayList<Object> args = new ArrayList<>();
                args.add(exception);
                for (Class<?> param : otherParams) {
                    if (param.isAssignableFrom(HttpServletRequest.class))
                        args.add(request);
                    else if (param.isAssignableFrom(HttpServletResponse.class))
                        args.add(response);
                    else if (param.isAssignableFrom(HttpSession.class))
                        args.add(request.getSession());
                    else if (param.isAssignableFrom(ServletContext.class))
                        args.add(request.getServletContext());
                    else args.add(null);
                }
                res.set((R<String>) m.invoke(this, args.toArray()));
            } catch (Exception ignored) { }
            finally {
                m.setAccessible(b);
            }
        });

        if (Objects.isNull(res.get()))
        {
            LoggerUtils.logErrorStackTrace( exception,log);
            return R.Builder.<String>getInstance()
                    .setCode(R.HttpCode.HTTP_ERR.get())
                    .setMessage(String.format("%s [%s]"
                            ,R.ErrorMessage.HTTP_ERR.get()
                            ,exception.getClass().getSimpleName()))
                    .setObj(TraceContext.traceId())
                    .build();
        }
        return res.get();
    }


}
