package my.restproto.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import my.restproto.common.restful.model.CommonResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理, 统一将异常转为 HTTP 状态码与 CommonResult 响应体
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    /** 拦截业务异常, HTTP 状态码取异常携带值 */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<CommonResult<?>> handleCommonException(CommonException ex) {
        return ResponseEntity.status(ex.getStatus()).body(CommonResult.fail(ex.getMessage(), ex.getData()));
    }

    /** 拦截 RequestBody 参数校验失败, 返回 400 与字段级错误信息 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResult<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(CommonResult.fail("参数校验失败", errors));
    }

    /** 拦截方法参数级校验失败, 返回 400 与字段级错误信息 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResult<?>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return ResponseEntity.badRequest().body(CommonResult.fail("参数校验失败", errors));
    }

    /** 拦截缺少请求参数, 提示用户补充缺失参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResult<?>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String msg = "缺少必要参数 " + ex.getParameterName();
        return ResponseEntity.badRequest().body(CommonResult.fail(msg));
    }

    /** 拦截请求资源不存在, 返回 404 */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonResult<?>> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonResult.fail("请求的资源不存在"));
    }

    /** 拦截请求方法不支持, 返回 405 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResult<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(CommonResult.fail("请求方法不支持"));
    }

    /** 拦截参数绑定异常, 返回 400 */
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<CommonResult<?>> handleBindingException(ServletRequestBindingException ex) {
        return ResponseEntity.badRequest().body(CommonResult.fail("参数绑定失败"));
    }

    /** 拦截请求体不可读, 返回 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResult<?>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(CommonResult.fail("请求体格式错误"));
    }

    /** 拦截参数类型不匹配, 返回 400 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResult<?>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(CommonResult.fail("参数类型不匹配"));
    }

    /** 拦截媒体类型不支持, 返回 415 */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<CommonResult<?>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(CommonResult.fail("媒体类型不支持"));
    }

    /** 兜底拦截未识别异常, 返回 500 并记录日志 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResult<?>> handleException(Exception ex) {
        log.error("系统异常: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResult.fail("系统异常, 请稍后重试"));
    }
}
