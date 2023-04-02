package xyz.chener.zp.common.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.chener.zp.common.config.CommonConfig;
import xyz.chener.zp.common.config.writeList.WriteListAutoConfig;
import xyz.chener.zp.common.entity.CommonVar;

import java.io.IOException;

@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final CommonConfig commonConfig;

    public static final String MICROSERVICE_CALL = "MICROSERVICE_CALL";

    public AuthFilter(@Qualifier("commonConfig") CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (writeListCheck(request)) {
            filterChain.doFilter(request,response);
            return;
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        if (serverCheck(request))
        {
            context.setAuthentication(new UsernamePasswordAuthenticationToken(
                    CommonVar.SERVICE_CALL_AUTH_NAME, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(CommonVar.SERVICE_CALL_AUTH_NAME)));
            SecurityContextHolder.setContext(context);
            filterChain.doFilter(request,response);
            return;
        }

        String user = request.getHeader(CommonVar.REQUEST_USER);
        String auth = request.getHeader(CommonVar.REQUEST_USER_AUTH);
        if (StringUtils.hasText(user) && StringUtils.hasText(auth))
        {
            context.setAuthentication(new UsernamePasswordAuthenticationToken(
                    user, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(auth)));
            SecurityContextHolder.setContext(context);
            filterChain.doFilter(request,response);
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

        for (String s : WriteListAutoConfig.writeList)
        {
            if (matchUrl(uri, s)) return true;
        }

        return false;
    }

    private boolean matchUrl(String uri, String s) {
        // 这里只处理了 单个{pathVariable} 和 /** 的情况
        if (s.contains("{") && s.contains("}"))
        {
            s = s.substring(0, s.indexOf("{")) + "**" + s.substring(s.indexOf("}") + 1);
        }
        if (s.contains("/**"))
        {
            int i = s.indexOf("/**");
            if (i == 0)
            {
                return true;
            } else
            {
                s = s.substring(0, i);
                if (uri.contains(s))
                {
                    return true;
                }
            }
        } else
        {
            if (uri.contains(s))
            {
                return true;
            }
        }
        return false;
    }

}
