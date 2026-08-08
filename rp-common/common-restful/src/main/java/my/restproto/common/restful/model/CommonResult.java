package my.restproto.common.restful.model;

import lombok.Data;

/**
 * 统一响应模型
 *
 * @param <T> 业务数据类型
 */
@Data
public class CommonResult<T> {

    /** 响应码, 0 表示成功, 1 表示失败 */
    private int code;

    /** 响应消息 */
    private String msg;

    /** 业务数据 */
    private T data;

    private CommonResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造成功响应
     *
     * @param data 业务数据
     */
    public static <T> CommonResult<T> ok(T data) {
        return new CommonResult<>(0, "ok", data);
    }

    /**
     * 构造无数据的成功响应
     */
    public static CommonResult<Void> ok() {
        return ok(null);
    }

    /**
     * 构造失败响应
     *
     * @param msg 失败消息
     */
    public static <T> CommonResult<T> fail(String msg) {
        return new CommonResult<>(1, msg, null);
    }

    /**
     * 构造携带附加数据的失败响应
     *
     * @param msg  失败消息
     * @param data 附加数据
     */
    public static <T> CommonResult<T> fail(String msg, T data) {
        return new CommonResult<>(1, msg, data);
    }
}
