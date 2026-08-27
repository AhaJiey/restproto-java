package my.restproto.common.mysql.config;

import lombok.extern.slf4j.Slf4j;
import my.restproto.common.mysql.properties.LazydogMySqlContainerProps;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.testcontainers.containers.MySQLContainer;

import java.util.Map;

/**
 * MySQL 懒狗容器配置, 显式开启且未配真实数据源时才注册
 */
@Slf4j
@ConditionalOnProperty(prefix = "restproto.lazydog.mysql", name = "enabled", havingValue = "true")
@Conditional(LazydogMySqlContainerConfig.LazydogCondition.class)
@EnableConfigurationProperties({
        LazydogMySqlContainerProps.class
})
@Configuration
public class LazydogMySqlContainerConfig {

    /** MySQL 懒狗容器 */
    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySqlContainer(LazydogMySqlContainerProps properties) {
        log.warn("懒狗配置, mysql 容器自动启动");
        return new MySQLContainer<>(properties.getImage())
                .withDatabaseName(properties.getDatabase())
                .withUsername(properties.getUsername())
                .withPassword(properties.getPassword());
    }

    /**
     * 检测 spring.datasource 是否配置
     */
    static class LazydogCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            if (!Binder.get(context.getEnvironment())
                    .bind("spring.datasource", Map.class)
                    .isBound()) {
                return true;
            }

            log.warn("配置了 restproto.lazydog.mysql.enabled = true, 但也配置了 spring.datasource, 懒狗配置将不再生效");
            return false;
        }
    }
}
