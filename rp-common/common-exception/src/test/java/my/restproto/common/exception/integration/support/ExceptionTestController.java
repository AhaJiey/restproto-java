package my.restproto.common.exception.integration.support;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import my.restproto.common.exception.CommonException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 集成测试端点, 触发全局异常处理器覆盖的各类异常
 */
@RestController
public class ExceptionTestController {

    @GetMapping("/common")
    public void common() {
        throw new CommonException(401, "未认证", "extra");
    }

    @GetMapping("/boom")
    public void boom() {
        throw new IllegalStateException("boom");
    }

    @PostMapping("/valid")
    public void valid(@Valid @RequestBody TestBody body) {
    }

    @GetMapping("/violation")
    public void violation(@Valid @RequestParam @NotBlank String name) {
    }

    @GetMapping("/missing-param")
    public void missingParam(@RequestParam String required) {
    }

    @GetMapping("/type-mismatch")
    public void typeMismatch(@RequestParam Long id) {
    }

    @PostMapping(value = "/json-only", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void jsonOnly(@RequestBody TestBody body) {
    }

    @Data
    public static class TestBody {

        @NotBlank
        private String name;
    }
}
