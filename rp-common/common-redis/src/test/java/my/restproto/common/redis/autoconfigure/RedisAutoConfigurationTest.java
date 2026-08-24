package my.restproto.common.redis.autoconfigure;

import com.redis.testcontainers.RedisContainer;
import my.restproto.common.redis.RedisOps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@DisplayName("redis 自动配置测试")
class RedisAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class,
                    // 与下方模块自动配置同名的 Spring Boot 自带配置, 用全限定名区分
                    org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
                    my.restproto.common.redis.autoconfigure.RedisAutoConfiguration.class)
            );

    @Test
    @DisplayName("默认装配 RedisOps")
    void shouldRegisterRedisOps() {
        contextRunner.run(context ->
                Assertions.assertThat(context).hasSingleBean(RedisOps.class));
    }

    @Test
    @DisplayName("懒狗容器默认不注册")
    void shouldNotRegisterLazydogContainerByDefault() {
        contextRunner.run(context ->
                Assertions.assertThat(context).doesNotHaveBean(RedisContainer.class));
    }

    @Test
    @DisplayName("懒狗容器显式开启且未配连接参数时注册")
    void shouldRegisterLazydogContainerWhenEnabled() {
        contextRunner
                .withPropertyValues("restproto.lazydog.redis.enabled=true")
                .run(context -> Assertions.assertThat(context).hasSingleBean(RedisContainer.class));
    }

    @Test
    @DisplayName("已配置连接参数时懒狗容器退让")
    void shouldBackOffLazydogContainerWhenConnectionConfigured() {
        contextRunner
                .withPropertyValues(
                        "restproto.lazydog.redis.enabled=true",
                        "spring.data.redis.host=localhost"
                )
                .run(context -> Assertions.assertThat(context).doesNotHaveBean(RedisContainer.class));
    }
}
