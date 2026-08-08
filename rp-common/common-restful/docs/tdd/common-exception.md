# common-exception 全局异常处理与响应模型

## 概述

业务异常 `CommonException` 与全局异常处理

## 正常路径

### 成功响应
- Given 请求 `GET /test/ok`
- When 接口正常返回
- Then 响应码 200, `code=0`, `data=hello`

## 异常路径

### 业务异常 CommonException
- Given 接口抛出 `CommonException(400, "业务异常")`
- When 请求该接口
- Then HTTP 状态码 400, `code=1`, `msg=业务异常`

### @RequestBody 参数校验失败
- Given 请求 `POST /test/valid` body 为 `{}`
- When 校验 `name` 非空失败
- Then HTTP 400, `data.name` 为校验消息

### 方法参数级校验失败
- Given 请求 `GET /test/constraint?id=0`
- When `@Min(1)` 校验失败
- Then HTTP 400, `code=1`

### 缺少请求参数
- Given 请求 `GET /test/missing` 且无 `name` 参数
- When 触发 `MissingServletRequestParameterException`
- Then HTTP 400, `msg=缺少必要参数 name`

### 缺少请求头
- Given 请求 `GET /test/header` 且无 `token` 请求头
- When 触发 `ServletRequestBindingException` 子类
- Then HTTP 400

### 请求资源不存在
- Given 请求未映射路径 `GET /not-exist`
- When 触发 `NoResourceFoundException`
- Then HTTP 404

### 请求方法不支持
- Given 请求 `POST /test/ok` 而接口仅支持 GET
- When 触发 `HttpRequestMethodNotSupportedException`
- Then HTTP 405

### 参数类型不匹配
- Given 请求 `GET /test/constraint?id=abc`
- When 触发 `MethodArgumentTypeMismatchException`
- Then HTTP 400

### 请求体不可读
- Given 请求 `POST /test/valid` 且 body 为非法 JSON
- When 触发 `HttpMessageNotReadableException`
- Then HTTP 400

### 媒体类型不支持
- Given 请求 `POST /test/valid` 且 Content-Type 为 text/plain
- When 触发 `HttpMediaTypeNotSupportedException`
- Then HTTP 415

### 全局兜底异常
- Given 接口抛出未识别运行时异常
- When 触发 `Exception` 兜底
- Then HTTP 500, `code=1`, 记录 error 日志

## 全局异常过滤器兜底

### 过滤器抛出普通运行时异常
- Given `GET /test/filter-runtime`, 过滤器链中抛出运行时异常
- When 异常逃逸出 MVC 层
- Then 由 `GlobalExceptionFilter` 兜底, HTTP 500, `code=1`, `msg=系统异常, 请稍后重试`

### 过滤器抛出业务异常
- Given `GET /test/filter-exception`, 过滤器链中抛出 `CommonException(400)`
- When 异常逃逸出 MVC 层
- Then HTTP 400, `code=1`, `msg` 透传业务异常消息
