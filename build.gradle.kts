plugins {
    id("java")
    id("checkstyle")
}

allprojects {
    group = "my.restproto"
    version = "v0.0.1"
}

subprojects {

    plugins.apply("java")
    plugins.apply("checkstyle")

    // Java 17 编译约束, 与 Spring Boot 3.5 基线一致
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    // 编译期开启 -parameters, 使 Spring MVC 可通过反射获取控制器方法参数名
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-parameters")
    }

    // 编辑风格配置
    checkstyle {
        toolVersion = rootProject.libs.versions.checkstyle.get()
        configFile = file("${rootProject.projectDir}/checkstyle.xml")
        isIgnoreFailures = false
        maxWarnings = Int.MAX_VALUE
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        // Lombok, 统一为各子模块提供编译期注解处理, 版本由 spring-boot-bom 平台约束管理
        compileOnly(platform(rootProject.libs.spring.boot.bom))
        annotationProcessor(platform(rootProject.libs.spring.boot.bom))
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
        testCompileOnly(platform(rootProject.libs.spring.boot.bom))
        testAnnotationProcessor(platform(rootProject.libs.spring.boot.bom))
        testCompileOnly(rootProject.libs.lombok)
        testAnnotationProcessor(rootProject.libs.lombok)

        testImplementation(platform(rootProject.libs.junit.bom))
        testImplementation(rootProject.libs.junit.jupiter)
        testRuntimeOnly(rootProject.libs.junit.platform.launcher)
    }

    tasks.test {
        useJUnitPlatform()
    }

}

tasks.register("ok") {
    doLast {
        println("OK!")
    }
}
