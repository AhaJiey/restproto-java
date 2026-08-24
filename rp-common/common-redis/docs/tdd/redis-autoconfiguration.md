# common-redis 自动装配 TDD 文档

## 功能概述与业务背景

`RedisAutoConfiguration` 是 common-redis 模块的自动配置入口, 通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册, 装配 `RedisOps` 与懒狗 Redis 容器配置。使用方引入依赖并配置 Redis 连接后, 自动获得带类型信息的 JSON 读写能力。

懒狗容器配置项前缀为 `restproto.lazydog.redis`:
- `enabled`: 是否启用, 默认 false
- `image`: 容器镜像, 默认 `redis:8.0`

## 正常路径

### 默认装配 RedisOps
- Given: 仅注册模块自动配置, 提供 ObjectMapper 与 StringRedisTemplate
- When: 启动应用上下文
- Then: 存在唯一 `RedisOps` bean

### 懒狗容器显式开启且未配 host 时注册
- Given: 配置 `enabled=true`, 未配置 `spring.data.redis.host`
- When: 启动应用上下文
- Then: 存在唯一 `RedisContainer` bean

## 边界情况与异常路径

### 懒狗容器默认不注册
- Given: 未配置 `restproto.lazydog.redis.enabled`
- When: 启动应用上下文
- Then: 不存在 `RedisContainer` bean

### 已配置连接参数时懒狗容器退让
- Given: 配置 `enabled=true` 且已配置 `spring.data.redis` 前缀连接参数
- When: 启动应用上下文
- Then: 不存在 `RedisContainer` bean
