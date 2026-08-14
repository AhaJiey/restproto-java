# common-mysql-entity 实体基类与软删除

## 概述

提供实体基类 `BaseEntity`:承载雪花主键 id、createTime/updateTime 审计字段与 `@TableLogic` 逻辑删除字段 deleteTime。未删除时 delete_time 为 null, 删除时由数据库 `now()` 写入删除时间。继承 `BaseEntity` 即启用软删除——deleteById 走逻辑删除置位, 查询自动过滤已删行, 无需全局配置。createTime/updateTime 由 MetaObjectHandler 自动填充。

## 正常路径

### 插入自动填充与雪花 id
- Given 实体继承 `BaseEntity`, 未手动设置 id/createTime/updateTime
- When 调用 `mapper.insert(entity)`
- Then id 生成雪花非空, createTime 与 updateTime 被填充

### 更新刷新 updateTime
- Given 已插入一行, 记录原 updateTime
- When 调用 `mapper.updateById(entity)`
- Then updateTime 更新为新值

### 软删除置位与查询过滤
- Given 表内存在一行有效数据
- When 调用 `mapper.deleteById(id)`
- Then delete_time 写入当前时间, 再次 `selectById` 返回空

## 异常路径

### 已删除记录不可见
- Given 一行已被软删除
- When 查询全表或分页
- Then 该行不出现, 分页 total 不包含已删记录

### 重复软删除返回 0
- Given 一行已被软删除
- When 再次对其 deleteById
- Then 返回影响行数 0 (where 条件含 delete_time IS NULL)

### 分页过滤已删除行
- Given 插入 10 行后软删除其中 3 行
- When 分页查询第一页
- Then total 为 7, 不包含已删记录
