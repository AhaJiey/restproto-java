# common-redis Redis 操作封装

## 概述

提供 `RedisOps` 操作类, 统一使用 `StringRedisTemplate`, value 以 JSON 字符串存储, 规避默认 JDK 序列化的兼容性问题。通过内部类 `Wrapper<T>` 承载类型标识与数据, 存储时记录 Jackson canonical 类型名, 读取时按类型标识还原, 避免反序列化类型丢失。使用方引入依赖后注入 `RedisOps` 即可存取, 无需关注序列化细节。

## 正常路径

### 自动装配注册 RedisOps
- Given 容器存在 `StringRedisTemplate` 与 `ObjectMapper`
- When 启动应用上下文
- Then `RedisOps` Bean 自动注册

### 简单类型存取
- Given Redis 可用, 调用方持有 `RedisOps`
- When `set(key, "value", String 的 TypeReference, duration)` 后 `get(key)`
- Then 返回原值, 类型为 String

### POJO 存取
- Given 自定义 POJO 类, 含基本类型字段
- When `set` 后 `get`
- Then 返回对象字段值与存入一致

### 泛型列表存取类型保留
- Given 数据为 `List<User>`, 元素含业务字段
- When `set` 后 `get`
- Then 返回 `List<User>`, 元素可访问 `User` 字段, 而非 Map

### 设置过期时间后读取
- Given `set` 指定 `Duration` 过期时长
- When 未到过期时间 `get`
- Then 返回存入数据

### 删除 key
- Given 已写入数据
- When `del(key)` 后 `get(key)`
- Then 返回 null

## 异常路径

### 不存在的 key
- Given 未写入任何数据
- When `get(key)`
- Then 返回 null, 不抛异常

### 自然过期后读取
- Given `set` 指定毫秒级过期时长
- When 用 Awaitility 轮询等待 key 过期后 `get(key)`
- Then 返回 null, 等待时长在一秒以内

## 使用说明

- 使用方自行配置 `spring.data.redis.*` 连接 Redis
- 未配置外部 Redis 连接时, 自动配置内建 redis:8.0 容器提供连接
- 过期场景测试使用 Awaitility, 等待时长控制在一秒以内
