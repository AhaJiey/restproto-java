# common-security 安全认证与授权

## 概述

基于 Spring Security 提供无状态安全链

URL 层放行全部请求, 授权交给 `@PreAuthorize` 注解判断; 认证失败与权限不足分别由注册到安全链的入口与拒绝处理器输出 `CommonResult`; auth 异常由安全框架处理而非被全局 500 兜底提前捕获。

## 正常路径

### 未认证请求放行
- Given 请求 `GET /security-test/public`
- When 未携带任何认证信息
- Then 响应码 200, `code=0`, `data=public`

### 已认证访问受保护资源
- Given 请求 `GET /security-test/user`, 该接口标注 `@PreAuthorize("isAuthenticated()")`
- When 携带已认证用户
- Then 响应码 200, `code=0`, `data=user`

### 具备角色访问受保护资源
- Given 请求 `GET /security-test/admin`, 该接口标注 `@PreAuthorize("hasRole('ADMIN')")`
- When 携带 ADMIN 角色用户
- Then 响应码 200, `code=0`, `data=admin`

### 具备权限访问受保护资源
- Given 请求 `GET /security-test/permission`, 该接口标注 `@PreAuthorize("hasAuthority('TEST_READ')")`
- When 携带 TEST_READ 权限用户
- Then 响应码 200, `code=0`, `data=permission`

## 异常路径

### 未认证访问受保护资源
- Given 请求 `GET /security-test/user`
- When 未携带任何认证信息, 触发 `@PreAuthorize` 拒绝
- Then 由 `UnAuthHandler` 输出 HTTP 401, `code=1`, `msg=未认证或认证已过期`

### 角色权限不足
- Given 请求 `GET /security-test/admin`
- When 携带非 ADMIN 角色用户, 触发 `@PreAuthorize` 拒绝
- Then 由 `DenyHandler` 输出 HTTP 403, `code=1`, `msg=无权限访问`

### 权限不足
- Given 请求 `GET /security-test/permission`
- When 携带无 TEST_READ 权限用户, 触发 `@PreAuthorize` 拒绝
- Then 由 `DenyHandler` 输出 HTTP 403, `code=1`, `msg=无权限访问`

### 控制器抛出 auth 异常
- Given 请求 `GET /security-test/auth-denied`, 控制器直接抛出 `AccessDeniedException`
- When auth 异常被 `AuthExceptionHandler` 捕获并重新上抛
- Then 由安全链按认证状态输出 401 或 403, 而非全局 500 兜底

## 安全配置细节

### 无会话
- Given 请求任意接口
- When 会话策略为 `SessionCreationPolicy.STATELESS`
- Then 请求不创建会话

### CSRF 关闭
- Given 发起 POST 请求
- When 未携带 CSRF Token
- Then 请求正常通过安全链

### CORS 关闭
- Given 请求携带跨域 Origin 头
- When 未配置 CORS
- Then 响应不包含 `Access-Control-Allow-Origin` 头
