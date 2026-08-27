plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.validation)

    // jackson 序列化, 提供响应体的 JSON 编解码
    implementation(libs.spring.boot.starter.json)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}