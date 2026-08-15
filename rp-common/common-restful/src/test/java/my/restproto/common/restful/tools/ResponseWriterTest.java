package my.restproto.common.restful.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.restproto.common.restful.model.CommonResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ResponseWriter 统一响应写入测试
 */
class ResponseWriterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ResponseWriter responseWriter = new ResponseWriter(objectMapper);

    /** 写入状态码与 JSON 响应体 */
    @Test
    void writeStatusAndBody() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        responseWriter.write(response, ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResult.fail("业务异常")));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith("application/json"));
        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertEquals(1, body.get("code").asInt());
        assertEquals("业务异常", body.get("msg").asText());
    }

    /** 写入响应头 */
    @Test
    void writeHeaders() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        responseWriter.write(response, ResponseEntity.status(HttpStatus.OK)
                .header("X-Custom", "abc")
                .body(CommonResult.ok("ok")));

        assertEquals("abc", response.getHeader("X-Custom"));
    }

    /** 响应已提交时跳过写入 */
    @Test
    void skipWhenCommitted() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setCommitted(true);

        responseWriter.write(response, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResult.fail("系统异常, 请稍后重试")));

        assertEquals(200, response.getStatus());
        assertEquals(0, response.getContentAsString().length());
    }

    /** 响应体为空时仅写状态码, 不写 JSON */
    @Test
    void writeWithoutBody() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        responseWriter.write(response, ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertNull(response.getContentType());
        assertEquals(0, response.getContentAsString().length());
    }
}
