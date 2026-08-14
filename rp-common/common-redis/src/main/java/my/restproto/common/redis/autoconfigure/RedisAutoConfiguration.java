package my.restproto.common.redis.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.restproto.common.redis.RedisOps;
import my.restproto.common.redis.config.RedisContainerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis 领域自动配置入口
 */
@AutoConfiguration
@Import(RedisContainerConfig.class)
public class RedisAutoConfiguration {

    /** Redis 操作类 */
    @Bean
    public RedisOps redisOps(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisOps(stringRedisTemplate, objectMapper);
    }
}
