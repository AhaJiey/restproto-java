# common-mysql 数据库连接与操作

## 概述

面向 MySQL 的公共数据模块, 内置 mysql-connector-j 驱动, 通过自动配置提供 MyBatis-Plus 分页拦截器。`MySqlAutoConfiguration` 注册分页拦截器, 使用方引入依赖并配置数据源即可获得分页查询能力。

## 正常路径

### 分页拦截器默认注册
- Given 使用方仅引入 common-mysql, 未自定义拦截器
- When 启动应用上下文
- Then `MybatisPlusInterceptor` 自动注册, 内含 `PaginationInnerInterceptor`, dbType 为 MySQL

### 分页配置项生效
- Given 配置 `restproto.pagination.max-limit=100`, `restproto.pagination.overflow=true`
- When 启动应用上下文
- Then 分页拦截器 maxLimit 为 100, overflow 为 true

### 分页查询返回正确结果
- Given 表中已插入 5 条数据, 使用 `Page(1, 2)` 查询
- When 调用 `mapper.selectPage(page, null)`
- Then 返回 total=5, records 共 2 条

### 页大小未超上限时原样返回
- Given 页大小 2 小于 maxLimit 500
- When 调用分页查询
- Then 返回 records 数量与请求页大小一致, 不被截断

## 异常路径

### 页大小超上限被截断
- Given `restproto.pagination.max-limit=5`, 请求页大小 10
- When 调用分页查询
- Then records 被截断为 5 条, 不触发越界

### 消费者自定义拦截器时模块让位
- Given 使用方自行声明 `MybatisPlusInterceptor` Bean
- When 启动应用上下文
- Then 模块默认拦截器不注册, 使用方定义生效

## 使用说明

- 使用方自行配置 `spring.datasource.*` 连接 MySQL, 分页配置项前缀为 `restproto.pagination`
- Mapper 接口标注 `@Mapper` 或在启动类使用 `@MapperScan` 扫描
