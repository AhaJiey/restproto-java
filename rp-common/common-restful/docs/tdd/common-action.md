# common-action 动态权限注解授权

## 概述

基于 Spring Security 提供 Action 注解式动态权限控制

用 `@Action` 注解标记接口所需权限, 启动类标注 `@ActionScan` 指定扫描包, `ActionCollector` 启动时收集全部 action 注册到注册表; 方法执行前由 AOP 切面校验当前用户 authorities 是否包含该权限值, 权限可动态授予与回收无需改代码; 未认证与权限不足分别由注册到安全链的入口与拒绝处理器输出 `CommonResult`。

## 正常路径

### 具备权限访问
- Given 请求 `GET /action-test/read`, 接口标注 `@Action("user:read")`
- When 携带 `user:read` 权限用户
- Then 响应码 200, `code=0`, `data=read`

### 未标注 Action 的接口不拦截
- Given 请求 `GET /action-test/plain`, 接口无注解
- When 未携带任何认证信息
- Then 响应码 200, `code=0`, `data=plain`

## 异常路径

### 未认证访问
- Given 请求 `GET /action-test/read`
- When 未携带任何认证信息, 切面校验失败
- Then 由 `UnAuthHandler` 输出 HTTP 401, `code=1`, `msg=未认证或认证已过期`

### 权限不足
- Given 请求 `GET /action-test/read`
- When 携带无 `user:read` 权限用户, 切面校验失败
- Then 由 `DenyHandler` 输出 HTTP 403, `code=1`, `msg=无权限访问`

### 权限错配
- Given 请求 `GET /action-test/write`, 接口标注 `@Action("user:write")`
- When 携带 `user:read` 权限用户
- Then 由 `DenyHandler` 输出 HTTP 403, `code=1`, `msg=无权限访问`

## 注册机制

### ActionScan 配置扫描包
- Given 启动类标注 `@ActionScan("com.example.controller")`
- When 应用启动
- Then `ActionCollector` 扫描该包下含 `@Action` 方法的类, 将 value 注册到 `ActionCollections`

### Action 注册收集
- Given 接口方法标注 `@Action("user:read")` 与 `@Action("user:write")`
- When 启动扫描
- Then `ActionCollections` 包含 `user:read` 与 `user:write`

### 无注解方法不注册
- Given 方法无 `@Action` 注解
- When 启动扫描
- Then 不产生对应注册条目
