package my.restproto.common.security;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试启动类, 供 SpringBootTest 启动应用上下文,
 * 额外扫描 restful.tools 以注入跨模块的 ResponseWriter
 */
@SpringBootApplication(scanBasePackages = {"my.restproto.common.security", "my.restproto.common.restful.tools"})
public class TestApplication {
}
