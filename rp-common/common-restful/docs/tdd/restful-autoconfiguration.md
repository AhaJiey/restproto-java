# RestfulAutoConfiguration 功能 TDD 文档

## 功能概述与业务背景

`RestfulAutoConfiguration` 是 restful 模块的自动配置入口, 通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册.

只在 servlet web 环境下装配 `ResponseWriter`, 应用直接注入就能用; 非 web 环境不注册.

## 正常路径

### servlet web 环境装配 ResponseWriter
- Given: servlet web 应用上下文, 注册 restful 与 jackson 自动配置, 并提供 `ServerProperties`
- When: 启动上下文
- Then: 存在唯一的 `ResponseWriter` bean

## 边界情况与异常路径

### 非 web 环境退让
- Given: 非 web 应用上下文, 注册 restful 自动配置
- When: 启动上下文
- Then: 不存在 `ResponseWriter` bean
