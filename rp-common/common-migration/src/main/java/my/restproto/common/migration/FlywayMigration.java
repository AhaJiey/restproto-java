package my.restproto.common.migration;

import my.restproto.common.migration.core.FlywayMigrationRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 按模块启用 Flyway 数据库迁移, 每个模块使用独立的迁移脚本目录与历史表
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FlywayMigrationRegistrar.class)
public @interface FlywayMigration {

    /** 业务模块标识, 决定迁移脚本目录 classpath:db/migration/{module} 与历史表名 */
    @AliasFor("name")
    String module() default "";

    /** module 的别名, 二选一 */
    @AliasFor("module")
    String name() default "";

    /** 迁移脚本目录, 完整 location 写法, 为空时回退 classpath:db/migration/{module} */
    String location() default "";

    /** 迁移历史表名, 为空时回退 {module 下划线化}_migration_history */
    String table() default "";

    /** baseline 起始版本 */
    String baselineVersion() default "0";

    /** 脚本目录缺失是否报错 */
    boolean failOnMissingLocations() default false;

    /** 脚本编码 */
    String encoding() default "UTF-8";
}
