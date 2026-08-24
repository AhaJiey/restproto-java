# common-exception 全局异常处理器 TDD 文档

## 功能概述与业务背景

`GlobalExceptionHandler` 通过 `@RestControllerAdvice` 将各类异常映射为 HTTP 状态码与 `CommonResult` 响应体, 避免异常细节暴露给客户端。

## 正常路径

### 业务异常按携带状态码输出
- Given: 抛出携带状态码与附加数据的 `CommonException`
- When: 全局异常处理器处理
- Then: 返回该状态码, 响应体为 `CommonResult.fail(msg, data)`

### 请求体参数校验失败返回字段级错误
- Given: `@RequestBody` 校验失败, 存在字段错误
- When: 全局异常处理器处理
- Then: 返回 400, data 为字段名到错误信息的映射

### 方法参数校验失败返回字段级错误
- Given: 方法参数校验失败
- When: 全局异常处理器处理
- Then: 返回 400, data 为字段名到错误信息的映射

## 边界情况与异常路径

### 缺少请求参数
- Given: 缺少必填请求参数
- When: 全局异常处理器处理
- Then: 返回 400, 消息提示缺失参数名

### 请求资源不存在
- Given: 请求路径无对应资源
- When: 全局异常处理器处理
- Then: 返回 404

### 请求方法不支持
- Given: 请求方法与端点允许的方法不一致
- When: 全局异常处理器处理
- Then: 返回 405

### 请求体格式错误
- Given: 请求体不可读
- When: 全局异常处理器处理
- Then: 返回 400

### 参数类型不匹配
- Given: 请求参数无法转换为目标类型
- When: 全局异常处理器处理
- Then: 返回 400

### 媒体类型不支持
- Given: 请求的 Content-Type 不在端点允许范围
- When: 全局异常处理器处理
- Then: 返回 415

### 未识别异常兜底 500
- Given: 抛出来自业务代码的未识别异常
- When: 全局异常处理器处理
- Then: 返回 500, 消息为通用系统异常提示
