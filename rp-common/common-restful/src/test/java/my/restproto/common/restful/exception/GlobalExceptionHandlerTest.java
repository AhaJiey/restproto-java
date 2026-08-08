package my.restproto.common.restful.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 全局异常处理测试
 */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    /** 正常返回: code 为 0 */
    @Test
    void ok() throws Exception {
        mockMvc.perform(get("/exception-test/ok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("hello"));
    }

    /** 业务异常: 状态码与消息透传 */
    @Test
    void commonException() throws Exception {
        mockMvc.perform(get("/exception-test/exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("业务异常"));
    }

    /** RequestBody 校验失败: 400 与字段级错误 */
    @Test
    void validFailed() throws Exception {
        mockMvc.perform(post("/exception-test/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.name").value("name 不能为空"));
    }

    /** 方法参数级校验失败: 400 */
    @Test
    void constraintViolation() throws Exception {
        mockMvc.perform(get("/exception-test/constraint").param("id", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 缺少请求参数: 400 */
    @Test
    void missingParameter() throws Exception {
        mockMvc.perform(get("/exception-test/missing"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("缺少必要参数 name"));
    }

    /** 缺少请求头: 400 */
    @Test
    void missingHeader() throws Exception {
        mockMvc.perform(get("/exception-test/header"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 资源不存在: 404 */
    @Test
    void notFound() throws Exception {
        mockMvc.perform(get("/not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 请求方法不支持: 405 */
    @Test
    void methodNotSupported() throws Exception {
        mockMvc.perform(post("/exception-test/ok"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 参数类型不匹配: 400 */
    @Test
    void typeMismatch() throws Exception {
        mockMvc.perform(get("/exception-test/constraint").param("id", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 请求体不可读: 400 */
    @Test
    void messageNotReadable() throws Exception {
        mockMvc.perform(post("/exception-test/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 媒体类型不支持: 415 */
    @Test
    void mediaTypeNotSupported() throws Exception {
        mockMvc.perform(post("/exception-test/valid")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 兜底异常: 500 */
    @Test
    void unknownException() throws Exception {
        mockMvc.perform(get("/exception-test/runtime"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(1));
    }
}
