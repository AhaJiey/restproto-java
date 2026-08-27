plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    // spring boot starter, 提供注解注册机制与迁移执行类
    implementation(libs.spring.boot.starter)

    // flyway, 提供数据库迁移能力, 透传给使用方
    api(libs.flyway.core)
    api(libs.flyway.mysql)

    // 复用 mysql 模块的容器配置, 提供 MySQL 测试数据源
    testImplementation(project(":rp-common:common-mysql"))
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.testcontainers.junit.jupiter)
}
