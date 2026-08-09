package my.restproto.common.security.action.support;

import my.restproto.common.restful.model.CommonResult;
import my.restproto.common.security.annotations.Action;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试触发接口, 用于覆盖 Action 扫描收集与切面鉴权
 */
@RestController
@RequestMapping("/action-test")
public class ActionTestController {

    /** 读取操作 */
    @GetMapping("/read")
    @Action("user:read")
    public CommonResult<String> read() {
        return CommonResult.ok("read");
    }

    /** 写入操作 */
    @GetMapping("/write")
    @Action("user:write")
    public CommonResult<String> write() {
        return CommonResult.ok("write");
    }

    /** 无注解方法 */
    @GetMapping("/plain")
    public CommonResult<String> plain() {
        return CommonResult.ok("plain");
    }
}
