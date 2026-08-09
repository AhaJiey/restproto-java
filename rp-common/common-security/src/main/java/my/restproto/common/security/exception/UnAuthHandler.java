package my.restproto.common.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.restproto.common.restful.model.CommonResult;
import my.restproto.common.restful.tools.ResponseWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 未认证入口, 输出 401 与 CommonResult 响应体
 */
@RequiredArgsConstructor
public class UnAuthHandler implements AuthenticationEntryPoint {

    /** 统一响应写入器 */
    private final ResponseWriter responseWriter;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        responseWriter.write(response,
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResult.fail("未认证或认证已过期")));
    }
}
