package my.restproto.common.security.integration.support;

import my.restproto.common.restful.model.CommonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 安全测试触发接口, 覆盖安全链配置与方法级注解授权
 */
@RestController
@RequestMapping("/security-test")
public class SecuritySanityController {

    /** 公共读接口, 验证 URL 层放行 */
    @GetMapping("/public")
    public CommonResult<String> publicGet() {
        return CommonResult.ok("public");
    }

    /** 公共写接口, 验证 CSRF 关闭 */
    @PostMapping("/public")
    public CommonResult<String> publicPost() {
        return CommonResult.ok("public");
    }

    /** 需登录访问的接口 */
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<String> user() {
        return CommonResult.ok("user");
    }

    /** 需 ADMIN 角色访问的接口 */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<String> admin() {
        return CommonResult.ok("admin");
    }

    /** 需 TEST_READ 权限访问的接口 */
    @GetMapping("/permission")
    @PreAuthorize("hasAuthority('TEST_READ')")
    public CommonResult<String> permission() {
        return CommonResult.ok("permission");
    }

    /** 直接抛出 auth 异常, 验证上抛安全链处理 */
    @GetMapping("/auth-denied")
    public CommonResult<String> authDenied() {
        throw new AccessDeniedException("无权限访问");
    }

    /** 直接抛出认证异常, 验证未认证走 401 */
    @GetMapping("/auth-failed")
    public CommonResult<String> authFailed() {
        throw new BadCredentialsException("认证失败");
    }
}
