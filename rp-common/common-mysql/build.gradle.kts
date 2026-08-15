plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    // jdbc 与 mybatis-plus, 提供数据源与数据库操作能力
    api(libs.spring.boot.starter.jdbc)
    api(libs.mybatis.plus.spring.boot3.starter)
    api(libs.mybatis.plus.jsqlparser)

    // mysql 驱动, 使使用方添加依赖即可连接 MySQL
    api(libs.mysql.connector.j)

    // testcontainers, 提供 MySQL 容器能力
    implementation(libs.spring.boot.testcontainers)
    implementation(libs.testcontainers.mysql)
    implementation(libs.testcontainers.junit.jupiter)

    // spring 测试
    testImplementation(libs.spring.boot.starter.test)
}
