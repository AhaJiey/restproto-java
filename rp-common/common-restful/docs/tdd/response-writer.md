# ResponseWriter 功能 TDD 文档

## 功能概述与业务背景

`ResponseWriter` 把 `ResponseEntity` 写进 `HttpServletResponse`, 统一输出 JSON 响应, 写完立即提交, 提交后任何修改都不再生效。

可以通过 `server.servlet.encoding.charset` 控制字符编码

## 正常路径

### 写出带体的响应
- Given: 未提交的响应, 状态码 200、带 JSON 体的 `ResponseEntity`
- When: 调用 `write`
- Then: 状态码 200, 内容类型 `application/json`, 响应体与期望 JSON 一致, 响应已提交

### 写出错误状态
- Given: 状态码 401、带 `CommonResult.unauth()` 体的 `ResponseEntity`
- When: 调用 `write`
- Then: 状态码 401, 响应体里 `code` 为 1, `msg` 为未认证提示

### 写出无体的响应
- Given: 状态码 204、无响应体的 `ResponseEntity`
- When: 调用 `write`
- Then: 状态码 204, 不设内容类型, 响应体为空, 响应已提交

### 写出响应头
- Given: 带单个响应头的 `ResponseEntity`
- When: 调用 `write`
- Then: 响应头原样写入

### 同名响应头多值保留
- Given: 同一头名下多个值
- When: 调用 `write`
- Then: 各值全部保留

### 写出时应用配置编码
- Given: `server.servlet.encoding.charset` 配成 `ISO-8859-1` 的 writer, 带体响应
- When: 调用 `write`
- Then: 响应字符编码为 `ISO-8859-1`

### 中文响应体不乱码
- Given: 带中文消息的失败响应
- When: 调用 `write`
- Then: 响应体中文原样保留, 响应编码为默认 `UTF-8`

## 边界情况与异常路径

### 响应已提交
- Given: 响应已提交
- When: 调用 `write`
- Then: 不做任何改动, 状态码与内容保持原样

### 连续两次写入
- Given: 首次 `write` 已提交响应
- When: 再次调用 `write`
- Then: 第二次不改动响应, 内容保持首次结果

### 序列化失败
- Given: 响应体为不可序列化对象
- When: 调用 `write`
- Then: 抛出 `IOException`, 响应不提交
