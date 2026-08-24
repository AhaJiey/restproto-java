# FlywayMigrationRegistrar 功能 TDD 文档

## 功能概述与业务背景

`FlywayMigrationRegistrar` 是 `@FlywayMigration` 注解的注册处理器, 按模块注册独立的 Flyway Bean 与迁移执行 Bean, 使各模块的脚本目录与历史表互相隔离。`module` 与 `name` 互为别名, 二选一即可; 均未指定或同一模块重复注册时拒绝启动。

## 正常路径

### module 属性注册独立迁移 bean
- Given: 注解指定 `module = "module-a"`
- When: 解析注解并注册
- Then: 注册 `module-aFlyway` 与 `module-aFlywayMigrationInitializer` bean, 启动时执行该模块迁移

### name 别名与 module 等价注册
- Given: 注解仅指定 `name = "module-a"`
- When: 解析注解并注册
- Then: 与指定 module 等价, 注册同名 bean

## 边界情况与异常路径

### module 与 name 均未指定时拒绝注册
- Given: 注解未指定 `module` 与 `name`
- When: 解析注解并注册
- Then: 抛出 `IllegalStateException`, 提示必须指定 module 或 name

### 同一模块重复注册时拒绝
- Given: 同一模块已注册过迁移
- When: 再次注册
- Then: 抛出 `IllegalStateException`, 提示模块已注册迁移
