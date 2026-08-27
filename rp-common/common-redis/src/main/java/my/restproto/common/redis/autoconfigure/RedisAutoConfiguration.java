package my.restproto.common.redis.autoconfigure;

import my.restproto.common.redis.RedisOps;
import my.restproto.common.redis.config.LazydogRedisContainerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 装配 Redis 懒狗容器与 RedisOps 操作类
 */
@AutoConfigureBefore({
        ServiceConnectionAutoConfiguration.class
})
@Import({
        LazydogRedisContainerConfig.class,
        RedisOps.class
})
@AutoConfiguration
public class RedisAutoConfiguration {
}
