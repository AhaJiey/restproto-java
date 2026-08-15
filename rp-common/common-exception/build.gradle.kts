plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    // 依赖 restful 基础模块, 透传 CommonResult 与响应写入能力
    api(project(":rp-common:common-restful"))

    // spring 测试
    testImplementation(libs.spring.boot.starter.test)
}
