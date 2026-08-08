package my.restproto.common.restful.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.restproto.common.restful.model.CommonResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 全局异常过滤器, 兜底拦截逃逸出 MVC 层的异常, 统一输出 CommonResult
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class GlobalExceptionFilter extends OncePerRequestFilter {

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CommonException ex) {
            writeResult(response, ex.getStatus(), CommonResult.fail(ex.getMessage(), ex.getData()));
        } catch (Exception ex) {
            log.error("逃逸异常: {}", ex.getMessage(), ex);
            writeResult(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, CommonResult.fail("系统异常, 请稍后重试"));
        }
    }

    /** 以 CommonResult 序列化 JSON 写回响应 */
    private void writeResult(HttpServletResponse response, int status, CommonResult<?> result) throws IOException {
        if (response.isCommitted()) {
            log.warn("响应已提交, 无法输出异常结果, 状态码 {}", status);
            return;
        }
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), result);
    }
}
