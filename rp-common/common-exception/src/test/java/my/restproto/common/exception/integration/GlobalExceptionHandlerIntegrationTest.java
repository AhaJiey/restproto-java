package my.restproto.common.exception.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayName("全局异常处理器集成测试")
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("业务异常按携带状态码输出")
    void shouldHandleCommonException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/common"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("未认证"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("extra"));
    }

    @Test
    @DisplayName("请求体参数校验失败返回字段级错误")
    void shouldHandleMethodArgumentNotValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": ""
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("参数校验失败"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").exists());
    }

    @Test
    @DisplayName("方法参数校验失败返回字段级错误")
    void shouldHandleMethodValidation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/violation").param("name", ""))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").exists());
    }

    @Test
    @DisplayName("缺少请求参数返回 400")
    void shouldHandleMissingParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/missing-param"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("缺少必要参数 required"));
    }

    @Test
    @DisplayName("请求资源不存在返回 404")
    void shouldHandleNoResourceFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/nonexistent"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("请求的资源不存在"));
    }

    @Test
    @DisplayName("请求方法不支持返回 405")
    void shouldHandleMethodNotSupported() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/common"))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("请求体格式错误返回 400")
    void shouldHandleMessageNotReadable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("请求体格式错误"));
    }

    @Test
    @DisplayName("参数类型不匹配返回 400")
    void shouldHandleTypeMismatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/type-mismatch").param("id", "abc"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("参数类型不匹配"));
    }

    @Test
    @DisplayName("媒体类型不支持返回 415")
    void shouldHandleMediaTypeNotSupported() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/json-only")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("x"))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("媒体类型不支持"));
    }

    @Test
    @DisplayName("未识别异常兜底返回 500")
    void shouldHandleUnknownException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/boom"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("系统异常, 请稍后重试"));
    }
}
