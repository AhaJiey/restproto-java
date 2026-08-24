# common-mysql 自动装配 TDD 文档

## 功能概述与业务背景

`MySqlAutoConfiguration` 是 common-mysql 模块的自动配置入口, 通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册, 装配 `MybatisPlusConfig`。使用方引入依赖并配置数据源后, 自动获得 MyBatis-Plus 分页拦截器与全表更新/删除防护。

分页配置项前缀为 `restproto.pagination`:
- `max-limit`: 单页条数上限, 默认 500, -1 表示不限制
- `overflow`: 当前页超出总页数时是否回退到第一页, 默认 false

懒狗容器配置项前缀为 `restproto.lazydog.mysql`:
- `enabled`: 是否启用, 默认 false
- `image`: 容器镜像, 默认 `mysql:8.0`
- `database`: 数据库名, 默认 `lazydog`
- `username`: 用户名, 默认 `lazydog`
- `password`: 密码, 默认 `lazydog`

## 正常路径

### 默认装配分页与全表防护拦截器
- Given: 仅注册模块自动配置
- When: 启动应用上下文
- Then: 存在唯一 `MybatisPlusInterceptor`, 内含 `PaginationInnerInterceptor` 与 `BlockAttackInnerInterceptor`, dbType 为 MYSQL

### 分页配置项反映到拦截器
- Given: 配置 `restproto.pagination.max-limit=100`、`restproto.pagination.overflow=true`
- When: 启动应用上下文
- Then: 拦截器 maxLimit 为 100, overflow 为 true

### 装配时间字段自动填充 handler
- Given: 默认应用上下文
- When: 启动应用上下文
- Then: 存在 `MetaObjectHandler` bean

### 懒狗容器显式开启且未配数据源时注册
- Given: 配置 `enabled=true`, 未配置 `spring.datasource`
- When: 启动应用上下文
- Then: 存在唯一 `MySQLContainer` bean, 数据库名与用户名来自配置项

## 边界情况与异常路径

### max-limit 为 -1 不限制
- Given: 配置 `restproto.pagination.max-limit=-1`
- When: 启动应用上下文
- Then: 拦截器 maxLimit 为 -1

### 懒狗容器默认不注册
- Given: 未配置 `restproto.lazydog.mysql.enabled`
- When: 启动应用上下文
- Then: 不存在 `MySQLContainer` bean

### 已配数据源时懒狗容器退让
- Given: 配置 `enabled=true` 且已配置 `spring.datasource.url`
- When: 启动应用上下文
- Then: 不存在 `MySQLContainer` bean
