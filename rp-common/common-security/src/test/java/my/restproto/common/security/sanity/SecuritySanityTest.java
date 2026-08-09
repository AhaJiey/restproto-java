package my.restproto.common.security.sanity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 安全模块测试: 安全链配置与方法级注解授权
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecuritySanityTest {

    @Autowired
    private MockMvc mockMvc;

    /** 未认证请求放行: URL 层全放行 */
    @Test
    void permitAll() throws Exception {
        mockMvc.perform(get("/security-test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("public"));
    }

    /** 无会话: 请求不创建会话 */
    @Test
    void statelessSession() throws Exception {
        mockMvc.perform(get("/security-test/public"))
                .andExpect(result -> assertNull(result.getRequest().getSession(false)));
    }

    /** CSRF 关闭: 无 Token 的 POST 正常通过 */
    @Test
    void csrfDisabled() throws Exception {
        mockMvc.perform(post("/security-test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /** CORS 关闭: 跨域请求不返回 CORS 头 */
    @Test
    void corsDisabled() throws Exception {
        mockMvc.perform(get("/security-test/public").header("Origin", "http://cors"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    /** 未认证访问受保护接口: 401 */
    @Test
    void unauthenticatedUser() throws Exception {
        mockMvc.perform(get("/security-test/user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("未认证或认证已过期"));
    }

    /** 已认证访问受保护接口: 200 */
    @Test
    @WithMockUser
    void authenticatedUser() throws Exception {
        mockMvc.perform(get("/security-test/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("user"));
    }

    /** 权限不足访问管理接口: 403 */
    @Test
    @WithMockUser(roles = "USER")
    void insufficientRole() throws Exception {
        mockMvc.perform(get("/security-test/admin"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("无权限访问"));
    }

    /** 具备 ADMIN 角色访问管理接口: 200 */
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminAccess() throws Exception {
        mockMvc.perform(get("/security-test/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("admin"));
    }

    /** 具备权限访问接口: 200 */
    @Test
    @WithMockUser(authorities = "TEST_READ")
    void permissionAccess() throws Exception {
        mockMvc.perform(get("/security-test/permission"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("permission"));
    }

    /** 无权限访问接口: 403 */
    @Test
    @WithMockUser
    void insufficientPermission() throws Exception {
        mockMvc.perform(get("/security-test/permission"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("无权限访问"));
    }

    /** 控制器抛出 auth 异常, 未认证上抛安全链: 401 */
    @Test
    void authDeniedUnauthenticated() throws Exception {
        mockMvc.perform(get("/security-test/auth-denied"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 控制器抛出 auth 异常, 已认证上抛安全链: 403 */
    @Test
    @WithMockUser(roles = "USER")
    void authDeniedAuthenticated() throws Exception {
        mockMvc.perform(get("/security-test/auth-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1));
    }

    /** 控制器抛出认证异常, 未认证上抛安全链: 401 */
    @Test
    void authFailedUnauthenticated() throws Exception {
        mockMvc.perform(get("/security-test/auth-failed"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("未认证或认证已过期"));
    }
}
