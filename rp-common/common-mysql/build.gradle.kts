plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    // jdbc
    api(libs.spring.boot.starter.jdbc)

    // mybatis-plus
    api(libs.mybatis.plus.spring.boot3.starter)

    // jsqlparser, 提供分页拦截器所需的 SQL 解析能力
    implementation(libs.mybatis.plus.jsqlparser)

    // mysql 驱动
    api(libs.mysql.connector.j)

    // testcontainers, 提供 MySQL 容器能力
    implementation(libs.spring.boot.testcontainers)
    implementation(libs.testcontainers.mysql)
    implementation(libs.testcontainers.junit.jupiter)

    testImplementation(libs.spring.boot.starter.test)
}
