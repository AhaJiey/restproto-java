plugins {
    id("java-library")
}

dependencies {
    // 通过 catalog 引入 Spring Boot BOM 作为平台约束, 统一管理本模块依赖版本
    api(platform(libs.spring.boot.bom))

    // 依赖 restful 基础模块, 透传 CommonResult 与响应写入能力
    api(project(":rp-common:common-restful"))

    // security, 提供无状态安全链与注解授权
    api(libs.spring.boot.starter.security)

    // AOP, 为 Action 动态权限切面提供代理支持
    api(libs.spring.boot.starter.aop)

    // Lombok, 各编译期配置独立声明平台约束以覆盖版本管理
    compileOnly(platform(libs.spring.boot.bom))
    annotationProcessor(platform(libs.spring.boot.bom))
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(platform(libs.spring.boot.bom))
    testAnnotationProcessor(platform(libs.spring.boot.bom))
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // spring 测试
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
}
