# common-mysql 集成测试 TDD 文档

## 功能概述与业务背景

`MybatisIntegrationTest` 覆盖分页、溢出、全表操作防护、软删除与实体时间字段自动填充在真实 MySQL 上的集成行为。懒狗容器开启 `restproto.lazydog.mysql.enabled=true`, 每个独立 Spring 上下文各起一个 MySQL 容器; 嵌套用例类用 `@TestPropertySource` 覆盖分页配置, 建表与数据准备在各用例类自己的 `setUp` 中完成, 保证用例间数据独立。

时间字段类型为 `Instant`, 对应数据库 TIMESTAMP 列, 填充值截断到秒。

## 正常路径

### 插入自动填充雪花 id 与时间
- Given: 实体未设置 id 与时间字段
- When: 调用 `insert`
- Then: id 生成雪花非空, createTime 与 updateTime 为同一时刻且截断到秒

### 部分更新时填充 updateTime
- Given: 仅带 id 与业务字段的实体, updateTime 为空
- When: 调用 `updateById`
- Then: updateTime 被填充为当前时刻, createTime 保持原值

### 分页返回正确 total 与 records
- Given: 表内 10 行数据, 请求第 1 页每页 2 条
- When: 执行分页查询
- Then: total 为 10, records 为 2 条

### 软删除置位且查询过滤
- Given: 表内存在一行有效数据
- When: 调用 `deleteById`
- Then: delete_time 非空, 再次 `selectById` 返回空

### 分页过滤已删行
- Given: 10 行数据软删除其中 3 行
- When: 执行分页查询
- Then: total 为 7, 不包含已删记录

### 重复软删除返回 0
- Given: 一行已被软删除
- When: 再次对其 `deleteById`
- Then: 返回影响行数 0

## 边界情况与异常路径

### 实体已带 updateTime 时更新不覆盖
- Given: 已插入实体, updateTime 非空
- When: 修改业务字段后调用 `updateById`
- Then: updateTime 保持原值, 不刷新

### 页大小超上限被截断
- Given: `max-limit=5`, 请求页大小 10
- When: 执行分页查询
- Then: records 截断为 5 条

### 当前页超出总页数且未开启溢出
- Given: `overflow=false`, 共 10 条 2 页, 请求第 100 页
- When: 执行分页查询
- Then: records 为空, total 仍为 10

### 当前页超出总页数且开启溢出
- Given: `overflow=true`, 共 10 条 2 页, 请求第 100 页
- When: 执行分页查询
- Then: 当前页回退到第一页, 返回第一页 5 条, total 仍为 10

### 无 WHERE 全表更新被拦截
- Given: 通过 Mapper 执行不带条件的更新
- When: 执行更新
- Then: 抛出 `MyBatisSystemException`, 根因是 `MybatisPlusException`
