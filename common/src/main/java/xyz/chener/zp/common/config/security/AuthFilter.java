package xyz.chener.zp.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.writeList.WriteListRegister;
import xyz.chener.zp.common.entity.CommonVar;
import xyz.chener.zp.common.entity.LoginUserDetails;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.ThreadUtils;
import xyz.chener.zp.common.utils.UriMatcherUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


// OncePerRequestFilter 会导致spring的异步请求 第二次进入时无法授权 报401
@Slf4j
public class AuthFilter implements Filter {

    private final CommonConfig commonConfig;

    public static final String MICROSERVICE_CALL = "MICROSERVICE_CALL";

    public AuthFilter(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (writeListCheck(request)) {
            ThreadUtils.runIgnoreException(() -> filterChain.doFilter(request,response));
            return;
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        if (serverCheck(request))
        {
            context.setAuthentication(new UsernamePasswordAuthenticationToken(
                    CommonVar.SERVICE_CALL_AUTH_NAME, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(CommonVar.SERVICE_CALL_AUTH_NAME)));
            SecurityContextHolder.setContext(context);
            ThreadUtils.runIgnoreException(() -> filterChain.doFilter(request,response));
            return;
        }

        String userBase64 = request.getHeader(CommonVar.REQUEST_USER);
        String user = userBase64 == null ? null : new String(Base64.getDecoder().decode(userBase64), StandardCharsets.UTF_8);

        AtomicReference<LoginUserDetails> loginUserDetails = new AtomicReference<>(null);
        ThreadUtils.runIgnoreException(() -> {
            String userObjectBase64 = request.getHeader(CommonVar.REQUEST_USER_OBJECT);
            if (StringUtils.hasText(userObjectBase64))
            {
                loginUserDetails.set(new ObjectMapper().readValue(Base64.getDecoder().decode(userObjectBase64), LoginUserDetails.class));
            }
        });

        String auth = request.getHeader(CommonVar.REQUEST_USER_AUTH);
        if (StringUtils.hasText(user) && StringUtils.hasText(auth) && Objects.nonNull(loginUserDetails.get()) && ObjectUtils.nullSafeEquals(loginUserDetails.get().getUsername(),user))
        {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(auth));
            usernamePasswordAuthenticationToken.setDetails(loginUserDetails.get());
            context.setAuthentication(usernamePasswordAuthenticationToken);
            SecurityContextHolder.setContext(context);
            ThreadUtils.runIgnoreException(() -> filterChain.doFilter(request,response));
            return;
        }

        SecurityContextHolder.clearContext();
        filterChain.doFilter(request,response);
    }

    private boolean serverCheck(HttpServletRequest request)
    {
        String header = request.getHeader(CommonVar.OPEN_FEIGN_HEADER);
        return StringUtils.hasText(header) && header.equals(commonConfig.getSecurity().getFeignCallSlat());
    }


    private boolean writeListCheck(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        for (String s : commonConfig.getSecurity().getWriteList())
        {
            if (matchUrl(uri, s)) return true;
        }

        for (String s : WriteListRegister.writeList)
        {
            if (matchUrl(uri, s)) return true;
        }

        return false;
    }

    private boolean matchUrl(String uri, String s) {
        if (s.contains("{") && s.contains("}"))
        {
            s = s.substring(0, s.indexOf("{")) + "*" + s.substring(s.indexOf("}") + 1);
        }
        return UriMatcherUtils.match(s,uri);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilterInternal((HttpServletRequest) request,(HttpServletResponse) response,chain);
    }
}
