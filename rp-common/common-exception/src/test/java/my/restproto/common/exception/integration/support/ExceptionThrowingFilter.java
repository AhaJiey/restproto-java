package my.restproto.common.exception.integration.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.restproto.common.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 集成测试过滤器, 在 MVC 层之前抛异常, 供全局异常过滤器捕获
 */
@Component
public class ExceptionThrowingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals("/filter/common")) {
            throw new CommonException(401, "未认证", "extra");
        }
        if (request.getRequestURI().equals("/filter/boom")) {
            throw new IllegalStateException("boom");
        }
        filterChain.doFilter(request, response);
    }
}
