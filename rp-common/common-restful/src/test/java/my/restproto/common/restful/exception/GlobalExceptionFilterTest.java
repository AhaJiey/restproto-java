package my.restproto.common.restful.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 全局异常过滤器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionFilterTest {

    @Autowired
    private MockMvc mockMvc;

    /** 兜底拦截过滤器抛出的普通异常: 500 */
    @Test
    void filterRuntimeException() throws Exception {
        mockMvc.perform(get("/test/filter-runtime"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("系统异常, 请稍后重试"));
    }

    /** 兜底拦截过滤器抛出的业务异常: 透传状态码与消息 */
    @Test
    void filterCommonException() throws Exception {
        mockMvc.perform(get("/test/filter-exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("过滤器业务异常"));
    }
}
