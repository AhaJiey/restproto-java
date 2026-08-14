package my.restproto.common.mysql.integration;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import my.restproto.common.mysql.integration.support.TestEntity;
import my.restproto.common.mysql.integration.support.TestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MyBatis-Plus 集成测试: 实体增删改查、自动填充与分页
 */
@SpringBootTest(properties = "restproto.pagination.max-limit=5")
class MybatisIntegrationTest {

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    List<TestEntity> examples = IntStream.range(0,10)
            .mapToObj(idx -> {
                TestEntity entity = new TestEntity();
                entity.setSomeField("test - %s".formatted(idx));
                return entity;
            })
            .toList();

    /** 每个用例前重建测试表并插入 10 条数据 */
    @BeforeEach
    void insertTestData() {
        // 重建测试表, 保证用例间数据独立
        jdbcTemplate.execute("DROP TABLE IF EXISTS tests");
        jdbcTemplate.execute("""
                CREATE TABLE tests (
                    id BIGINT PRIMARY KEY,
                    some_field VARCHAR(64) NOT NULL,
                    create_time DATETIME,
                    update_time DATETIME,
                    delete_time DATETIME
                )
                """);

        testMapper.insert(examples);
    }

    /** 插入自动填充雪花 id 与时间字段 */
    @Test
    void insertFillsBaseFields() {
        TestEntity entity = new TestEntity();
        entity.setSomeField("fill-test");
        testMapper.insert(entity);

        TestEntity loaded = testMapper.selectById(entity.getId());

        assertThat(loaded.getId()).isNotNull();
        assertThat(loaded.getCreateTime()).isNotNull();
        assertThat(loaded.getUpdateTime()).isNotNull();
        assertThat(loaded.getDeleteTime()).isNull();
    }

    /** 普通更新刷新 updateTime */
    @Test
    void updateRefreshesUpdateTime() {
        TestEntity entity = examples.get(0);
        LocalDateTime before = entity.getUpdateTime();

        entity.setSomeField("updated");
        testMapper.updateById(entity);

        TestEntity loaded = testMapper.selectById(entity.getId());

        assertThat(loaded.getSomeField()).isEqualTo("updated");
        assertThat(loaded.getUpdateTime()).isAfterOrEqualTo(before);
    }

    /** 分页返回正确 total 与 records */
    @Test
    void paginationWorks() {
        Page<TestEntity> page = testMapper.selectPage(new Page<>(1, 2), null);

        assertThat(page.getTotal()).isEqualTo(10);
        assertThat(page.getRecords()).hasSize(2);
    }

    /** 页大小超上限被截断为 maxLimit */
    @Test
    void maxLimitEnforced() {
        Page<TestEntity> page = testMapper.selectPage(new Page<>(1, 10), null);

        assertThat(page.getRecords()).hasSize(5);
    }

    /** 软删除写入 delete_time, 查询自动过滤 */
    @Test
    void softDeleteMarksAndFilters() {
        TestEntity entity = examples.get(0);

        testMapper.deleteById(entity.getId());

        assertThat(testMapper.selectById(entity.getId())).isNull();
        // 验证数据库写入逻辑删除时间
        assertThat(jdbcTemplate.queryForObject(
                "SELECT delete_time FROM tests WHERE id = ?",
                LocalDateTime.class, entity.getId())).isNotNull();
    }

    /** 分页过滤已删除行 */
    @Test
    void deletedRowsExcludedFromPagination() {
        // 软删除其中 3 行
        examples.subList(0, 3).forEach(entity -> testMapper.deleteById(entity.getId()));

        Page<TestEntity> page = testMapper.selectPage(new Page<>(1, 2), null);

        assertThat(page.getTotal()).isEqualTo(7);
    }

    /** 重复软删除返回 0 */
    @Test
    void repeatDeleteReturnsZero() {
        TestEntity entity = examples.get(0);

        assertThat(testMapper.deleteById(entity.getId())).isEqualTo(1);
        assertThat(testMapper.deleteById(entity.getId())).isZero();
    }
}
