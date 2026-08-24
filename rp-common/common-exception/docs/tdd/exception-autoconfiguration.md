# common-exception 自动装配 TDD 文档

## 功能概述与业务背景

`ExceptionAutoConfiguration` 是 common-exception 模块的自动配置入口, 通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册, 装配全局异常处理器与异常过滤器。使用方引入依赖后, 自动获得统一异常响应能力。

## 正常路径

### 默认装配全局异常处理器与过滤器
- Given: 仅注册模块自动配置, 提供 ResponseWriter
- When: 启动应用上下文
- Then: 存在 `GlobalExceptionHandler` 与 `GlobalExceptionFilter` bean
