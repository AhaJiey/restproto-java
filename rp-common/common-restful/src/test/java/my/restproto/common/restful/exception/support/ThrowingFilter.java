package my.restproto.common.restful.exception.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.restproto.common.restful.exception.CommonException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 测试专用过滤器, 按路径抛出异常, 用于验证全局异常过滤器的兜底拦截
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ThrowingFilter extends OncePerRequestFilter {

    /** 触发普通运行时异常的路径 */
    public static final String RUNTIME_PATH = "/test/filter-runtime";

    /** 触发业务异常的路径 */
    public static final String EXCEPTION_PATH = "/test/filter-exception";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (RUNTIME_PATH.equals(uri)) {
            throw new IllegalStateException("filter boom");
        }
        if (EXCEPTION_PATH.equals(uri)) {
            throw new CommonException(400, "过滤器业务异常");
        }
        filterChain.doFilter(request, response);
    }
}
