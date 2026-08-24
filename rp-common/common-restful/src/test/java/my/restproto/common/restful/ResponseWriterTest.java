package my.restproto.common.restful;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.json.JsonAssert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@DisplayName("ResponseWriter 类测试")
class ResponseWriterTest {

    private final ResponseWriter writer = new ResponseWriter(new ServerProperties(), new ObjectMapper());

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("写出带体的响应")
    void shouldWriteBody() throws Exception {
        ResponseEntity<CommonResult<String>> entity = ResponseEntity.ok(CommonResult.ok("payload"));
        String expected = """
                {
                  "code": 0,
                  "msg": "ok",
                  "data": "payload"
                }
                """;

        writer.write(response, entity);

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_JSON_VALUE);
        Assertions.assertThat(response.isCommitted()).isTrue();
        JsonAssert.comparator(JSONCompareMode.STRICT)
                .assertIsMatch(expected, response.getContentAsString());

    }

    @Test
    @DisplayName("写出错误状态")
    void shouldWriteErrorStatus() throws Exception {
        ResponseEntity<CommonResult<Void>> entity = ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResult.unauth());
        String expected = """
                {
                  "code": 1,
                  "msg": "未认证或认证已过期",
                  "data": null
                }
                """;

        writer.write(response, entity);

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        JsonAssert.comparator(JSONCompareMode.STRICT)
                .assertIsMatch(expected, response.getContentAsString());
    }

    @Test
    @DisplayName("写出无体的响应")
    void shouldWriteWithoutBody() throws Exception {
        ResponseEntity<Void> entity = ResponseEntity.noContent().build();

        writer.write(response, entity);

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        Assertions.assertThat(response.getContentType()).isNull();
        Assertions.assertThat(response.getContentAsString()).isEmpty();
        Assertions.assertThat(response.isCommitted()).isTrue();
    }

    @Test
    @DisplayName("携带响应头")
    void shouldWriteHeader() throws IOException {
        ResponseEntity<CommonResult<Void>> entity = ResponseEntity.ok()
                .header("X-Trace", "t-1")
                .body(CommonResult.ok());

        writer.write(response, entity);

        Assertions.assertThat(response.getHeader("X-Trace")).isEqualTo("t-1");
    }

    @Test
    @DisplayName("同名头多值保留")
    void shouldKeepMultiValueHeader() throws IOException {
        ResponseEntity<CommonResult<Void>> entity = ResponseEntity.ok()
                .header("X-Tag", "a", "b")
                .body(CommonResult.ok());

        writer.write(response, entity);

        Assertions.assertThat(response.getHeaders("X-Tag")).containsExactly("a", "b");
    }

    @Test
    @DisplayName("中文响应体不乱码")
    void shouldKeepChinese() throws Exception {
        ResponseEntity<CommonResult<Void>> entity = ResponseEntity.ok(CommonResult.fail("这是一段中文消息"));

        writer.write(response, entity);

        Assertions.assertThat(response.getContentAsString()).contains("这是一段中文消息");
    }

    @Test
    @DisplayName("已提交则放弃写入")
    void shouldSkipWhenCommitted() throws IOException {
        response.setCommitted(true);

        writer.write(response, ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResult.unauth()));

        // Mock 响应默认状态码是 200, 写入被跳过时保持默认
        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    @DisplayName("连续两次写入, 后一次不改动响应")
    void shouldIgnoreSecondWrite() throws Exception {
        writer.write(response, ResponseEntity.ok(CommonResult.ok("first")));
        String first = response.getContentAsString();

        writer.write(response, ResponseEntity.ok(CommonResult.ok("second")));

        Assertions.assertThat(response.getContentAsString()).isEqualTo(first);
    }

    @Test
    @DisplayName("序列化失败上抛")
    void shouldThrowWhenBodyNotSerializable() {
        // 裸 Object 没有可序列化的属性, ObjectMapper 无法序列化
        ResponseEntity<Object> entity = ResponseEntity.ok(new Object());

        Assertions.assertThatThrownBy(() -> writer.write(response, entity))
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("编码可配置")
    void shouldApplyConfiguredEncoding() throws Exception {
        ServerProperties properties = new ServerProperties();
        properties.getServlet().getEncoding().setCharset(StandardCharsets.ISO_8859_1);

        ResponseWriter configured = new ResponseWriter(properties, new ObjectMapper());

        ResponseEntity<CommonResult<Void>> entity = ResponseEntity.ok(CommonResult.ok());

        configured.write(response, entity);

        Assertions.assertThat(response.getCharacterEncoding()).isEqualTo(StandardCharsets.ISO_8859_1.toString());
    }
}
