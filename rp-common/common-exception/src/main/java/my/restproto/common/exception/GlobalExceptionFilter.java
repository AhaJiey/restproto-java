package my.restproto.common.exception;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.restproto.common.restful.model.CommonResult;
import my.restproto.common.restful.tools.ResponseWriter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 全局异常过滤器, 兜底拦截逃逸出 MVC 层的异常, 统一输出 CommonResult
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class GlobalExceptionFilter extends OncePerRequestFilter {

    /** 统一响应写入器 */
    private final ResponseWriter responseWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CommonException ex) {
            responseWriter.write(response,
                    ResponseEntity.status(ex.getStatus()).body(CommonResult.fail(ex.getMessage(), ex.getData())));
        } catch (Exception ex) {
            log.error("逃逸异常: {}", ex.getMessage(), ex);
            responseWriter.write(response, ResponseEntity
                    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(CommonResult.fail("系统异常, 请稍后重试"))
            );
        }
    }
}
