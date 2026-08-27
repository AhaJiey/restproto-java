package my.restproto.common.restful;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> {

    /** 响应码, 0 表示成功, 1 表示失败 */
    private int code;

    private String msg;

    /** 成功时返回业务数据, 失败时返回错误明细, 或者 null */
    private T data;

    public static <T> CommonResult<T> ok(T data) {
        return new CommonResult<>(0, "ok", data);
    }

    public static CommonResult<Void> ok() {
        return ok(null);
    }

    public static <T> CommonResult<T> fail(String msg) {
        return new CommonResult<>(1, msg, null);
    }

    public static <T> CommonResult<T> fail(String msg, T data) {
        return new CommonResult<>(1, msg, data);
    }

    public static CommonResult<Void> sysBoom() {
        return fail("系统异常, 请稍后重试");
    }

    public static CommonResult<Void> unauth() {
        return fail("未认证或认证已过期");
    }

    public static CommonResult<Void> accessDeny() {
        return fail("无权限访问");
    }
}
