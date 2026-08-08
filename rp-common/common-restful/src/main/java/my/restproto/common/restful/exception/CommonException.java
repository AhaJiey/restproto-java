package my.restproto.common.restful.exception;

import lombok.Getter;

/**
 * 业务异常, 携带 HTTP 状态码与附加数据
 */
@Getter
public class CommonException extends RuntimeException {

    /** HTTP 状态码 */
    private final int status;

    /** 附加数据 */
    private final Object data;

    /**
     * 构造业务异常
     *
     * @param status HTTP 状态码
     * @param msg    异常消息
     */
    public CommonException(int status, String msg) {
        this(status, msg, null);
    }

    /**
     * 构造携带附加数据的业务异常
     *
     * @param status HTTP 状态码
     * @param msg    异常消息
     * @param data   附加数据
     */
    public CommonException(int status, String msg, Object data) {
        super(msg);
        this.status = status;
        this.data = data;
    }
}
