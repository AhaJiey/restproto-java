package my.restproto.common.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis 懒狗容器配置项
 */
@Data
@ConfigurationProperties(prefix = "restproto.lazydog.redis")
public class LazydogRedisContainerProps {

    /** 是否启用 Redis 懒狗容器 */
    private boolean enabled = false;

    /** 容器镜像 */
    private String image = "redis:8.0";
}
