package my.restproto.common.restful.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.restproto.common.restful.model.CommonResult;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 未认证入口, 输出 401 与 CommonResult 响应体
 */
@Component
@RequiredArgsConstructor
public class UnAuthHandler implements AuthenticationEntryPoint {

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        writeResult(response, HttpServletResponse.SC_UNAUTHORIZED, CommonResult.fail("未认证或认证已过期"));
    }

    /** 以 CommonResult 序列化 JSON 写回响应 */
    private void writeResult(HttpServletResponse response, int status, CommonResult<?> result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), result);
    }
}
