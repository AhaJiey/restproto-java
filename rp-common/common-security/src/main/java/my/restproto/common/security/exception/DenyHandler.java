package my.restproto.common.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.restproto.common.restful.model.CommonResult;
import my.restproto.common.restful.tools.ResponseWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /** 统一响应写入器 */
    private final ResponseWriter responseWriter;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        responseWriter.write(response,
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonResult.fail("无权限访问")));
    }
}
