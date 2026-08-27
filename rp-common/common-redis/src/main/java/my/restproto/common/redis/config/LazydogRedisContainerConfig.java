package my.restproto.common.redis.config;

import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import my.restproto.common.redis.properties.LazydogRedisContainerProps;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * Redis 懒狗容器配置, 显式开启且未配真实实例时才注册
 */
@Slf4j
@ConditionalOnProperty(prefix = "restproto.lazydog.redis", name = "enabled", havingValue = "true")
@Conditional(LazydogRedisContainerConfig.LazydogCondition.class)
@EnableConfigurationProperties({
        LazydogRedisContainerProps.class
})
@Configuration
public class LazydogRedisContainerConfig {

    /** Redis 懒狗容器 */
    @Bean
    @ServiceConnection(name = "redis")
    public RedisContainer redisContainer(LazydogRedisContainerProps properties) {
        log.warn("懒狗配置, redis 容器自动启动");

        return new RedisContainer(properties.getImage());
    }

    /**
     * 检测 spring.data.redis 是否配置
     */
    static class LazydogCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            if (!Binder.get(context.getEnvironment())
                    .bind("spring.data.redis", Map.class)
                    .isBound()) {
                return true;
            }

            log.warn("配置了 restproto.lazydog.redis.enabled = true, 但也配置了 spring.data.redis, 懒狗配置将不再生效");
            return false;
        }
    }
}
