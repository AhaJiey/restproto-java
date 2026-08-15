plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    // Web 与参数校验能力, 作为公共基础透传给下游业务模块
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.validation)

    // spring 测试
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}