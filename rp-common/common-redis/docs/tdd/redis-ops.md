# common-redis RedisOps TDD 文档

## 功能概述与业务背景

`RedisOps` 在 `StringRedisTemplate` 之上提供带类型信息的 JSON 读写。写入时把数据与 canonical 类型名一起装箱, 读取时校验请求类型与存储类型一致后还原, 因此 List 等泛型数据也能原样读回, 并支持过期时间。

## 正常路径

### 写入后按原类型还原
- Given: 任意类型数据
- When: `set` 后按相同类型 `get`
- Then: 还原为原值, 类型一致

### 泛型 List 还原
- Given: `List<String>` 数据
- When: 按 `TypeReference<List<String>>` 写入后按相同类型读取
- Then: 还原为元素类型正确的 List

### 删除后读取返回 null
- Given: key 已写入
- When: `del` 后 `get`
- Then: 返回 null

## 边界情况与异常路径

### 不存在的 key 返回 null
- Given: key 从未写入
- When: 按指定类型 `get`
- Then: 返回 null

### data 为 null 时返回 null
- Given: `set` 时 data 为 null
- When: `get`
- Then: 返回 null

### 过期后返回 null
- Given: 带短过期时间的写入
- When: 超过 TTL 后 `get`
- Then: 返回 null

### 缺少类型标识抛异常
- Given: 存储的 JSON 无 `clazz` 字段
- When: `get`
- Then: 抛出 `IllegalStateException`

### 非法 JSON 抛异常
- Given: 存储值不是合法 JSON
- When: `get`
- Then: 抛出 `IllegalStateException`

### 请求类型与存储类型不一致时抛异常
- Given: 已按 `TypeReference<String>` 写入
- When: 按 `TypeReference<Integer>` 读取
- Then: 抛出 `IllegalStateException`
