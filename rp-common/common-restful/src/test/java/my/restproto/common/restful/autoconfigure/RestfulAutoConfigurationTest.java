package my.restproto.common.restful.autoconfigure;

import my.restproto.common.restful.ResponseWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@DisplayName("RestfulAutoConfiguration 自动配置测试")
class RestfulAutoConfigurationTest {

    private final WebApplicationContextRunner webRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RestfulAutoConfiguration.class, JacksonAutoConfiguration.class))
            .withUserConfiguration(ServerPropertiesConfig.class);

    private final ApplicationContextRunner plainRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RestfulAutoConfiguration.class));

    @Test
    @DisplayName("servlet web 环境装配 ResponseWriter")
    void shouldRegisterResponseWriterInServletContext() {
        webRunner.run(context -> {
            Assertions.assertThat(context).hasSingleBean(ResponseWriter.class);
        });
    }

    @Test
    @DisplayName("非 web 环境不装配 ResponseWriter")
    void shouldNotRegisterResponseWriterWithoutWebApp() {
        plainRunner.run(context ->
                Assertions.assertThat(context).doesNotHaveBean(ResponseWriter.class));
    }

    // 自动配置不注册 ServerProperties, 这里补一个给 ResponseWriter 提供构造参数
    @Configuration(proxyBeanMethods = false)
    static class ServerPropertiesConfig {

        @Bean
        ServerProperties serverProperties() {
            return new ServerProperties();
        }
    }
}
