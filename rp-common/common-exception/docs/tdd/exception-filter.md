# common-exception 全局异常过滤器 TDD 文档

## 功能概述与业务背景

`GlobalExceptionFilter` 作为最高优先级过滤器, 兜底拦截逃逸出 MVC 层的异常, 统一输出 `CommonResult`, 防止异常信息泄漏到响应。

## 正常路径

### 业务异常按携带状态码输出
- Given: 过滤器链路抛出 `CommonException`
- When: 过滤器处理
- Then: 响应状态码为异常携带值, 响应体为 `CommonResult`

### 未识别异常按 500 输出
- Given: 过滤器链路抛出未识别异常
- When: 过滤器处理
- Then: 响应状态码为 500, 消息为通用系统异常提示
