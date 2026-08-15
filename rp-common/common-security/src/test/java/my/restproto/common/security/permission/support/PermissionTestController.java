package my.restproto.common.security.permission.support;

import my.restproto.common.restful.model.CommonResult;
import my.restproto.common.security.annotations.Permission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试触发接口, 用于覆盖 Permission 扫描收集与切面鉴权
 */
@RestController
@RequestMapping("/permission-test")
public class PermissionTestController {

    /** 读取权限 */
    @GetMapping("/read")
    @Permission("user:read")
    public CommonResult<String> read() {
        return CommonResult.ok("read");
    }

    /** 写入权限 */
    @GetMapping("/write")
    @Permission("user:write")
    public CommonResult<String> write() {
        return CommonResult.ok("write");
    }

    /** 无注解方法 */
    @GetMapping("/plain")
    public CommonResult<String> plain() {
        return CommonResult.ok("plain");
    }
}
