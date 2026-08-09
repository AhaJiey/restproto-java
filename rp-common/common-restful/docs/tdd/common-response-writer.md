# common-response-writer 统一响应写入

## 概述

基于 Spring 提供 filter 框架的统一 HTTP 响应写入, 将 ResponseEntity 序列化写回 HttpServletResponse

`ResponseWriter` 组件接收 HttpServletResponse 与 ResponseEntity, 依次写状态码、响应头与 JSON 响应体, 响应已提交则跳过写入。供 GlobalExceptionFilter、UnAuthHandler、DenyHandler 等 filter 场景复用, 消除各处理器重复的序列化逻辑。

## 正常路径

### 写入状态码与 JSON 响应体
- Given 构建 ResponseEntity(status=400, body=CommonResult.fail("业务异常"))
- When 调用 ResponseWriter.write(response, entity)
- Then 响应状态码为 400, Content-Type 为 application/json, 响应体为对应 CommonResult JSON

### 写入响应头
- Given ResponseEntity 携带自定义响应头
- When 调用 write
- Then 响应头原样写回

## 异常路径

### 响应已提交
- Given HttpServletResponse 已提交 (isCommitted=true)
- When 调用 write
- Then 不写任何内容, 保持原响应

### 响应体为空
- Given ResponseEntity 无 body
- When 调用 write
- Then 仅写状态码与响应头, 不设置 Content-Type, 不写响应体
