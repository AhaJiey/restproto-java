plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    // security, 提供无状态安全链与认证上下文
    api(libs.spring.boot.starter.security)

    // 依赖 restful, 提供统一响应模型与响应写入能力
    implementation(project(":rp-common:common-restful"))

    // common-redis, 提供令牌撤销黑名单
    implementation(project(":rp-common:common-redis"))

    // jjwt, 版本显式声明, 提供令牌签发与解析
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // spring 测试
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}
