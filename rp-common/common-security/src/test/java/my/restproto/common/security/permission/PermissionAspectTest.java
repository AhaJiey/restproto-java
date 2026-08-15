package my.restproto.common.security.permission;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Permission 切面鉴权测试
 */
@SpringBootTest
@AutoConfigureMockMvc
class PermissionAspectTest {

    @Autowired
    private MockMvc mockMvc;

    /** 具备权限访问: 200 */
    @Test
    @WithMockUser(authorities = "user:read")
    void authorized() throws Exception {
        mockMvc.perform(get("/permission-test/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("read"));
    }

    /** 已认证无权限: 403 */
    @Test
    @WithMockUser
    void unauthorized() throws Exception {
        mockMvc.perform(get("/permission-test/read"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("无权限访问"));
    }

    /** 未认证访问: 401 */
    @Test
    void anonymous() throws Exception {
        mockMvc.perform(get("/permission-test/read"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("未认证或认证已过期"));
    }

    /** 权限错配: 403 */
    @Test
    @WithMockUser(authorities = "user:read")
    void mismatchPermission() throws Exception {
        mockMvc.perform(get("/permission-test/write"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 无注解方法不被拦截: 匿名访问 200 */
    @Test
    void unannotatedNotIntercepted() throws Exception {
        mockMvc.perform(get("/permission-test/plain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("plain"));
    }
}
