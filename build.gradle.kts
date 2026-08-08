plugins {
    id("java")
}

allprojects {
    group = "my.restproto"
    version = "v0.0.1"
}

subprojects {

    plugins.apply("java")

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

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
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
