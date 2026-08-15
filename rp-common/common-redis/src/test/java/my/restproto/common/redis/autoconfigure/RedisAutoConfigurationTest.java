package my.restproto.common.redis.autoconfigure;

import my.restproto.common.redis.RedisOps;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * redis 自动配置测试: RedisOps Bean 注册
 */
class RedisAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(
                            org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
                            JacksonAutoConfiguration.class,
                            RedisAutoConfiguration.class
                    ));

    /** 默认注册 RedisOps Bean */
    @Test
    void redisOpsRegistered() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(RedisOps.class));
    }
}
