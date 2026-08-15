package my.restproto.common.mysql.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

/**
 * MySQL 测试容器配置, 使用方在测试或开发环境显式 @Import 启用
 */
@Configuration
public class MySqlContainerConfig {

    /** MySQL 容器 Bean, 未配置数据源时启用并自动连接容器 */
    @Bean
    @ServiceConnection
    @ConditionalOnMissingBean(DataSource.class)
    public MySQLContainer<?> mySqlContainer() {
        return new MySQLContainer<>("mysql:8.0");
    }
}
