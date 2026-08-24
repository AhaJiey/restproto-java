package my.restproto.common.exception.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayName("全局异常过滤器集成测试")
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("业务异常按携带状态码输出")
    void shouldWriteCommonException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/filter/common"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("未认证"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("extra"));
    }

    @Test
    @DisplayName("未识别异常按 500 输出")
    void shouldWriteInternalError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/filter/boom"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("系统异常, 请稍后重试"));
    }
}
