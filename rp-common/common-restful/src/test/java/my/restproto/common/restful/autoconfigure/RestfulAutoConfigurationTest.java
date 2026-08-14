package my.restproto.common.restful.autoconfigure;

import my.restproto.common.restful.tools.ResponseWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * restful 自动配置导入测试, 验证无 basePackage 场景下组件注册
 */
class RestfulAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class,
                    RestfulAutoConfiguration.class
            ));

    /** 自动配置注册统一响应写入组件 */
    @Test
    void registersComponents() {
        runner.run(context -> Assertions.assertThat(context).hasSingleBean(ResponseWriter.class));
    }
}
