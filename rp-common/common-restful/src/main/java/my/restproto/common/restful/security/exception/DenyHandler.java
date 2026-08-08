package my.restproto.common.restful.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.restproto.common.restful.model.CommonResult;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 权限拒绝处理器, 输出 403 与 CommonResult 响应体
 */
@Component
@RequiredArgsConstructor
public class DenyHandler implements AccessDeniedHandler {

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        writeResult(response, HttpServletResponse.SC_FORBIDDEN, CommonResult.fail("无权限访问"));
    }

    /** 以 CommonResult 序列化 JSON 写回响应 */
    private void writeResult(HttpServletResponse response, int status, CommonResult<?> result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), result);
    }
}
