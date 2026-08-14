package my.restproto.common.mysql.autoconfigure;

import my.restproto.common.mysql.config.MySqlContainerConfig;
import my.restproto.common.mysql.config.MybatisPlusConfig;
import my.restproto.common.mysql.properties.PaginationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * mysql 领域自动配置入口
 */
@AutoConfiguration
@Import({MybatisPlusConfig.class, MySqlContainerConfig.class})
@EnableConfigurationProperties(PaginationProperties.class)
public class MySqlAutoConfiguration {
}
