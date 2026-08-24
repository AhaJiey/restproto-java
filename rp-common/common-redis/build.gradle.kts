plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    // spring data redis, 提供 Redis 连接与模板能力
    api(libs.spring.boot.starter.data.redis)

    // jackson 序列化, 提供 JSON 编解码与时间类型支持
    implementation(libs.spring.boot.starter.json)

    // testcontainers, 提供 Redis 容器能力
    implementation(libs.spring.boot.testcontainers)
    implementation(libs.testcontainers)
    implementation(libs.testcontainers.junit.jupiter)
    implementation(libs.testcontainers.redis)

    // awaitility, 异步等待断言
    testImplementation(libs.awaitility)

    testImplementation(libs.spring.boot.starter.test)
}
