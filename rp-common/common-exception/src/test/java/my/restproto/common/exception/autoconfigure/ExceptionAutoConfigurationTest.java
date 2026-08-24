package my.restproto.common.exception.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.restproto.common.exception.GlobalExceptionFilter;
import my.restproto.common.exception.GlobalExceptionHandler;
import my.restproto.common.restful.ResponseWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@DisplayName("exception 自动配置测试")
class ExceptionAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(ResponseWriter.class,
                    () -> new ResponseWriter(new ServerProperties(), new ObjectMapper()))
            .withConfiguration(AutoConfigurations.of(ExceptionAutoConfiguration.class));

    @Test
    @DisplayName("默认装配全局异常处理器与过滤器")
    void shouldRegisterHandlerAndFilter() {
        contextRunner.run(context -> {
            Assertions.assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            Assertions.assertThat(context).hasSingleBean(GlobalExceptionFilter.class);
        });
    }
}
