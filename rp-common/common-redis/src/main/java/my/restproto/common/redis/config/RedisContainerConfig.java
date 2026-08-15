package my.restproto.common.redis.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Redis 测试容器配置, 未配置连接工厂时由自动配置启用
 */
@Configuration
public class RedisContainerConfig {

    /** Redis 容器 Bean, 未配置连接工厂时启用并自动连接容器 */
    @Bean
    @ServiceConnection(name = "redis")
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisContainer redisContainer() {
        return new RedisContainer("redis:8.0");
    }
}
