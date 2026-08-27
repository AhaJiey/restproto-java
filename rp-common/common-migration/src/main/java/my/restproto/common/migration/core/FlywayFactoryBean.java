package my.restproto.common.migration.core;

import lombok.RequiredArgsConstructor;
import my.restproto.common.migration.properties.FlywayMigrationProps;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Locale;

/**
 * 按模块构建 Flyway, 脚本目录与历史表均以模块隔离
 */
@RequiredArgsConstructor
public class FlywayFactoryBean implements FactoryBean<Flyway> {

    /** 按模块捕获的迁移配置 */
    private final FlywayMigrationProps config;

    /** 迁移目标数据源 */
    private final DataSource dataSource;

    @Override
    public Flyway getObject() {
        // 脚本目录未指定时按模块回退
        String location = StringUtils.hasText(config.getLocation())
                ? config.getLocation()
                : defaultLocation(config.getModule());
        // 历史表未指定时按模块下划线化回退
        String table = StringUtils.hasText(config.getTable())
                ? config.getTable()
                : defaultHistoryTable(config.getModule());
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(location)
                .table(table)
                .baselineOnMigrate(true)
                .baselineVersion(config.getBaselineVersion())
                .failOnMissingLocations(config.isFailOnMissingLocations())
                .encoding(config.getEncoding())
                .load();
    }

    @Override
    public Class<?> getObjectType() {
        return Flyway.class;
    }

    /** 把模块标识中的连字符转为下划线, 生成独立历史表名 */
    private String defaultHistoryTable(String module) {
        return module.toLowerCase(Locale.ROOT).replace("-", "_") + "_migration_history";
    }

    /** 按模块拼出默认脚本目录 */
    private String defaultLocation(String module) {
        return "classpath:db/migration/" + module;
    }
}
