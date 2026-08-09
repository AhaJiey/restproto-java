package my.restproto.common.security.autoconfigure;

import my.restproto.common.restful.autoconfigure.RestfulAutoConfiguration;
import my.restproto.common.security.SecurityConfig;
import my.restproto.common.security.action.ActionAspect;
import my.restproto.common.security.action.ActionCollections;
import my.restproto.common.security.exception.AuthExceptionHandler;
import my.restproto.common.security.exception.DenyHandler;
import my.restproto.common.security.exception.UnAuthHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.web.SecurityFilterChain;

/**
 * security 自动配置导入测试, 验证无 basePackage 场景下组件注册
 */
class SecurityAutoConfigurationTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class,
                    RestfulAutoConfiguration.class,
                    SecurityAutoConfiguration.class
            ));

    /** 自动配置注册安全链与授权组件 */
    @Test
    void registersComponents() {
        runner.run(context -> {
            Assertions.assertThat(context).hasSingleBean(SecurityFilterChain.class);
            Assertions.assertThat(context).hasSingleBean(SecurityConfig.class);
            Assertions.assertThat(context).hasSingleBean(UnAuthHandler.class);
            Assertions.assertThat(context).hasSingleBean(DenyHandler.class);
            Assertions.assertThat(context).hasSingleBean(AuthExceptionHandler.class);
            Assertions.assertThat(context).hasSingleBean(ActionCollections.class);
            Assertions.assertThat(context).hasSingleBean(ActionAspect.class);
        });
    }
}
