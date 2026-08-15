package my.restproto.common.restful.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

/**
 * 统一响应写入器, 将 ResponseEntity 依次写状态码/响应头/JSON 响应体, 供 filter 框架复用
 */
@Slf4j
@RequiredArgsConstructor
public class ResponseWriter {

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /** 将响应实体写入 HTTP 响应, 响应已提交则跳过 */
    public void write(HttpServletResponse response, ResponseEntity<?> entity) throws IOException {
        if (response.isCommitted()) {
            log.warn("响应已提交, 无法输出响应体, 状态码 {}", entity.getStatusCode().value());
            return;
        }
        response.setStatus(entity.getStatusCode().value());
        entity.getHeaders().forEach((name, values) -> values.forEach(value -> response.setHeader(name, value)));
        if (entity.getBody() != null) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), entity.getBody());
        }
    }
}
