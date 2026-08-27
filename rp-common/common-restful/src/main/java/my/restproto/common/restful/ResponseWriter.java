package my.restproto.common.restful;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

/**
 * ResponseWriter, 让 HttpServletResponse 返回 Json 数据
 */
@Slf4j
@RequiredArgsConstructor
public class ResponseWriter {

    private final ServerProperties properties;

    private final ObjectMapper objectMapper;

    /** 将 ResponseEntity 写入 HttpServletResponse */
    public void write(HttpServletResponse response, ResponseEntity<?> entity) throws IOException {
        // response 如果已经提交, 做任何修改无法生效
        if (response.isCommitted()) {
            log.warn("响应已提交, 无法输出响应体, 状态码 {}", entity.getStatusCode().value());
            return;
        }

        writeStatus(response, entity.getStatusCode());
        writeHeader(response, entity.getHeaders());

        Object body = entity.getBody();
        if (body != null) {
            writeContentType(response);
            writeBody(response, body);
        }

        response.flushBuffer();
    }

    private void writeStatus(HttpServletResponse response, HttpStatusCode status) throws IOException {
        response.setStatus(status.value());
    }

    private void writeHeader(HttpServletResponse response, HttpHeaders headers) {
        headers.forEach((name, values) -> {
            for (String value : values) {
                response.addHeader(name, value);
            }
        });
    }

    private void writeContentType(HttpServletResponse response) {
        String contentType = MediaType.APPLICATION_JSON_VALUE;
        String charset = properties.getServlet().getEncoding().getCharset().toString();
        response.setContentType(contentType);
        response.setCharacterEncoding(charset);
    }

    private void writeBody(HttpServletResponse response, Object obj) throws IOException {
        objectMapper.writeValue(response.getWriter(), obj);
    }
}
