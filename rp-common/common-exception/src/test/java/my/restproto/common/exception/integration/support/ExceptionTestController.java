package my.restproto.common.exception.integration.support;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import my.restproto.common.exception.CommonException;
import my.restproto.common.restful.model.CommonResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试触发接口, 用于覆盖全局异常处理的各分支
 */
@Validated
@RestController
@RequestMapping("/exception-test")
public class ExceptionTestController {

    /** 正常返回接口 */
    @GetMapping("/ok")
    public CommonResult<String> ok() {
        return CommonResult.ok("hello");
    }

    /** 触发 CommonException */
    @GetMapping("/exception")
    public CommonResult<String> exception() {
        throw new CommonException(400, "业务异常");
    }

    /** 触发 RequestBody 参数校验失败 */
    @PostMapping("/valid")
    public CommonResult<String> valid(@Valid @RequestBody TestReq req) {
        return CommonResult.ok("ok");
    }

    /** 触发方法参数级校验失败 */
    @GetMapping("/constraint")
    public CommonResult<String> constraint(@RequestParam @Min(1) int id) {
        return CommonResult.ok("ok");
    }

    /** 触发缺少请求参数 */
    @GetMapping("/missing")
    public CommonResult<String> missing(@RequestParam String name) {
        return CommonResult.ok(name);
    }

    /** 触发缺少请求头 */
    @GetMapping("/header")
    public CommonResult<String> header(@RequestHeader String token) {
        return CommonResult.ok(token);
    }

    /** 触发兜底异常 */
    @GetMapping("/runtime")
    public CommonResult<String> runtime() {
        throw new IllegalStateException("boom");
    }

    /** 校验请求体模型 */
    @Data
    public static class TestReq {

        /** 业务名称 */
        @NotBlank(message = "name 不能为空")
        private String name;
    }
}
