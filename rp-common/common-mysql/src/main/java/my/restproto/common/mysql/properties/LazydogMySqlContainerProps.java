package my.restproto.common.mysql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * mysql 懒狗容器配置项, 默认关闭
 */
@Data
@ConfigurationProperties(prefix = "restproto.lazydog.mysql")
public class LazydogMySqlContainerProps {

    /** 是否启用 MySQL 懒狗容器 */
    private boolean enabled = false;

    /** 容器镜像 */
    private String image = "mysql:8.0";

    /** 数据库名 */
    private String database = "lazydog";

    /** 用户名 */
    private String username = "lazydog";

    /** 密码 */
    private String password = "lazydog";
}
