package my.restproto.common.mysql.autoconfigure;

import my.restproto.common.mysql.config.LazydogMySqlContainerConfig;
import my.restproto.common.mysql.config.MybatisPlusConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.testcontainers.service.connection.ServiceConnectionAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 装配 MyBatis-Plus 配置与懒狗 MySQL 容器配置
 */
@AutoConfigureBefore({
        ServiceConnectionAutoConfiguration.class
})
@Import({
        MybatisPlusConfig.class,
        LazydogMySqlContainerConfig.class
})
@AutoConfiguration
public class MySqlAutoConfiguration {
}
