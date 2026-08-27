package my.restproto.common.migration.properties;

import lombok.Builder;
import lombok.Data;

/**
 * Flyway 按模块迁移的配置, 由 registrar 捕获注解属性后传入工厂
 */
@Data
@Builder
public class FlywayMigrationProps {

    /** 业务模块标识 */
    private String module;

    /** 迁移脚本目录, 完整 location 写法, 为空时由工厂回退 */
    private String location;

    /** 迁移历史表名, 为空时由工厂回退 */
    private String table;

    /** baseline 起始版本 */
    private String baselineVersion;

    /** 脚本目录缺失是否报错 */
    private boolean failOnMissingLocations;

    /** 脚本编码 */
    private String encoding;
}
