package my.restproto.common.migration.integration;

import my.restproto.common.migration.FlywayMigration;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@DisplayName("Flyway 按模块迁移集成测试")
@SpringBootTest(properties = {
        "restproto.lazydog.mysql.enabled=true"
})
class FlywayMigrationIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("module-aFlyway")
    private Flyway moduleAFlyway;

    @Autowired
    @Qualifier("module-bFlyway")
    private Flyway moduleBFlyway;

    @Autowired
    @Qualifier("module-cFlyway")
    private Flyway moduleCFlyway;

    @Test
    @DisplayName("各模块迁移各自执行")
    void shouldMigrateEachModuleIndependently() {
        Assertions.assertThat(countTable("module_a_migration")).isEqualTo(1);
        Assertions.assertThat(countTable("module_b_migration")).isEqualTo(1);
        Assertions.assertThat(countTable("module_c_migration")).isEqualTo(1);
    }

    @Test
    @DisplayName("版本线完整")
    void shouldRecordCompleteVersionLine() {
        Assertions.assertThat(hasVersion("module_a_migration_history", "1")).isTrue();
        Assertions.assertThat(hasVersion("module_a_migration_history", "2")).isTrue();
        Assertions.assertThat(hasVersion("custom_b_migration_history", "1")).isTrue();
        Assertions.assertThat(hasVersion("module_c_migration_history", "1")).isTrue();
    }

    @Test
    @DisplayName("历史表独立且显式表名生效")
    void shouldKeepHistoryTablesIsolated() {
        Assertions.assertThat(countTable("module_b_migration_history")).isZero();
        Assertions.assertThat(countAppliedVersions("module_a_migration_history")).isEqualTo(2);
        Assertions.assertThat(countAppliedVersions("custom_b_migration_history")).isEqualTo(1);
        Assertions.assertThat(countAppliedVersions("module_c_migration_history")).isEqualTo(1);
    }

    @Test
    @DisplayName("默认脚本目录与历史表回退")
    void shouldUseDefaultLocationAndTable() {
        Configuration configuration = moduleAFlyway.getConfiguration();

        Assertions.assertThat(configuration.getLocations())
                .extracting(Location::getPath)
                .containsExactly("db/migration/module-a");
        Assertions.assertThat(configuration.getTable()).isEqualTo("module_a_migration_history");
    }

    @Test
    @DisplayName("显式脚本目录与历史表优先")
    void shouldUseExplicitLocationAndTable() {
        Configuration configuration = moduleBFlyway.getConfiguration();

        Assertions.assertThat(configuration.getLocations())
                .extracting(Location::getPath)
                .containsExactly("db/migration/custom-b");
        Assertions.assertThat(configuration.getTable()).isEqualTo("custom_b_migration_history");
    }

    @Test
    @DisplayName("迁移配置随模块传入")
    void shouldPassThroughMigrationSettings() {
        assertMigrationSettings(moduleAFlyway, false, StandardCharsets.UTF_8);
        assertMigrationSettings(moduleBFlyway, false, StandardCharsets.UTF_8);
        assertMigrationSettings(moduleCFlyway, true, StandardCharsets.ISO_8859_1);
    }

    @Test
    @DisplayName("共享 schema 时后执行模块自动 baseline")
    void shouldBaselineWhenModulesShareSchema() {
        int baselineRows = countBaselineRows("module_a_migration_history")
                + countBaselineRows("custom_b_migration_history")
                + countBaselineRows("module_c_migration_history");

        Assertions.assertThat(baselineRows).isEqualTo(2);
    }

    private void assertMigrationSettings(
            Flyway flyway, boolean failOnMissingLocations, Charset encoding
    ) {
        Configuration configuration = flyway.getConfiguration();

        Assertions.assertThat(configuration.isBaselineOnMigrate()).isTrue();
        Assertions.assertThat(configuration.getBaselineVersion().getVersion()).isEqualTo("0");
        Assertions.assertThat(configuration.isFailOnMissingLocations()).isEqualTo(failOnMissingLocations);
        Assertions.assertThat(configuration.getEncoding()).isEqualTo(encoding);
    }

    private int countTable(String tableName) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class, tableName);
    }

    private boolean hasVersion(String historyTable, String version) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + historyTable + " WHERE version = ?",
                Integer.class, version);
        return count != null && count > 0;
    }

    private int countAppliedVersions(String historyTable) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + historyTable + " WHERE type = 'SQL'",
                Integer.class);
    }

    private int countBaselineRows(String historyTable) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + historyTable + " WHERE version = '0' AND type = 'BASELINE'",
                Integer.class);
    }

    @TestConfiguration
    @FlywayMigration(module = "module-a")
    public static class ModuleAConfig {
    }

    @TestConfiguration
    @FlywayMigration(module = "module-b", location = "classpath:db/migration/custom-b", table = "custom_b_migration_history")
    public static class ModuleBConfig {
    }

    @TestConfiguration
    @FlywayMigration(name = "module-c", failOnMissingLocations = true, encoding = "ISO-8859-1")
    public static class ModuleCConfig {
    }
}
