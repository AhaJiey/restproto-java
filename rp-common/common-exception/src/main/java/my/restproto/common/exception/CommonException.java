package my.restproto.common.exception;

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

    public CommonException(int status, String msg) {
        this(status, msg, null);
    }

    public CommonException(int status, String msg, Object data) {
        super(msg);
        this.status = status;
        this.data = data;
    }
}
