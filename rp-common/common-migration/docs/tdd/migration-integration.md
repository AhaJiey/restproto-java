# Flyway 按模块迁移集成 TDD 文档

## 功能概述与业务背景

在真实 MySQL 容器上验证 `@FlywayMigration` 与 `FlywayFactoryBean` 的端到端行为: 每个模块使用独立脚本目录与历史表, 互不污染; 显式配置覆盖默认回退; baseline、目录缺失校验与编码等配置随模块传入 Flyway; 多模块共享数据库时靠 baseline 避免非空 schema 冲突。

## 正常路径

### 各模块迁移各自执行
- Given: 开启 MySQL 容器, 三个模块分别使用默认配置、显式配置与 name 别名
- When: 启动应用上下文
- Then: 各模块脚本建出的表均存在

### 版本线完整
- Given: 模块带 V1、V2 两个脚本
- When: 启动应用上下文
- Then: 历史表按顺序记录 version 1 与 version 2

### 历史表独立且显式表名生效
- Given: 三个模块共享同一数据库
- When: 启动应用上下文
- Then: 各模块历史表只含自身脚本版本, 显式表名生效后默认表名不再创建

### 默认脚本目录与历史表回退
- Given: 仅指定模块 `module-a`, 模块标识含连字符
- When: 启动应用上下文
- Then: 脚本目录为 `classpath:db/migration/module-a`, 历史表为 `module_a_migration_history`, 连字符转为下划线

### 显式脚本目录与历史表优先
- Given: 指定 `location = "classpath:db/migration/custom-b"` 与 `table = "custom_b_migration_history"`
- When: 启动应用上下文
- Then: 使用显式目录与表名, 不回退

### 迁移配置随模块传入
- Given: 未配置时使用默认值, 显式配置时按注解值传入
- When: 启动应用上下文
- Then: `baselineOnMigrate` 为 true, `baselineVersion` 默认 0, `failOnMissingLocations` 与 `encoding` 与注解一致

## 边界情况与异常路径

### 共享 schema 时后执行模块自动 baseline
- Given: 多个模块共享同一数据库
- When: 启动应用上下文
- Then: 首个执行模块不 baseline, 其余模块各自 baseline 到起始版本
