package my.restproto.common.mysql.integration;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import my.restproto.common.mysql.integration.support.TestEntity;
import my.restproto.common.mysql.integration.support.TestMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@DisplayName("MyBatis-Plus 集成测试")
@SpringBootTest(properties = {
        "restproto.lazydog.mysql.enabled=true"
})
class MybatisIntegrationTest {

    @Nested
    @TestPropertySource(properties = {
            "restproto.pagination.max-limit=5",
            "restproto.pagination.overflow=false"
    })
    @DisplayName("普通情况")
    class plainCases {

        @Autowired
        private TestMapper testMapper;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        private List<TestEntity> examples;

        @BeforeEach
        void setUp() {
            // 重建测试表, 保证用例间数据独立
            jdbcTemplate.execute("DROP TABLE IF EXISTS tests");
            jdbcTemplate.execute("""
                    CREATE TABLE tests (
                        id BIGINT PRIMARY KEY,
                        some_field VARCHAR(64) NOT NULL,
                        create_time TIMESTAMP NULL,
                        update_time TIMESTAMP NULL,
                        delete_time TIMESTAMP NULL
                    )
                    """);

            examples = new ArrayList<>();
            IntStream.range(0, 10).forEach(idx -> {
                examples.add(TestEntity.builder()
                        .someField("test-" + idx)
                        .build());
            });
            testMapper.insert(examples);
        }

        @Test
        @DisplayName("插入自动填充雪花 id 与时间字段")
        void shouldFillBaseFieldsOnInsert() {
            TestEntity entity = TestEntity.builder().someField("fill-test").build();

            testMapper.insert(entity);

            Assertions.assertThat(entity.getId()).isNotNull();
            Assertions.assertThat(entity.getCreateTime()).isNotNull();
            Assertions.assertThat(entity.getUpdateTime()).isEqualTo(entity.getCreateTime());
            Assertions.assertThat(entity.getDeleteTime()).isNull();
        }

        @Test
        @DisplayName("部分更新时填充 updateTime")
        void shouldFillUpdateTimeOnPartialUpdate() {
            TestEntity origin = examples.get(0);

            TestEntity partial = TestEntity.builder()
                    .id(origin.getId())
                    .someField("partial-update")
                    .build();
            testMapper.updateById(partial);

            TestEntity loaded = testMapper.selectById(origin.getId());

            Assertions.assertThat(loaded.getSomeField()).isEqualTo("partial-update");
            Assertions.assertThat(loaded.getUpdateTime()).isNotNull();
            Assertions.assertThat(loaded.getCreateTime()).isEqualTo(origin.getCreateTime());
        }

        @Test
        @DisplayName("实体已带 updateTime 时更新不覆盖")
        void shouldKeepExistingUpdateTimeOnUpdate() {
            TestEntity entity = examples.get(0);
            Instant before = entity.getUpdateTime();

            entity.setSomeField("updated");
            testMapper.updateById(entity);

            TestEntity loaded = testMapper.selectById(entity.getId());

            Assertions.assertThat(loaded.getSomeField()).isEqualTo("updated");
            Assertions.assertThat(loaded.getUpdateTime()).isEqualTo(before);
        }

        @Test
        @DisplayName("分页返回正确 total 与 records")
        void shouldReturnTotalAndRecordsForPagination() {
            Page<TestEntity> page = testMapper.selectPage(new Page<>(1, 2), null);

            Assertions.assertThat(page.getTotal()).isEqualTo(10L);
            Assertions.assertThat(page.getRecords()).hasSize(2);
        }

        @Test
        @DisplayName("页大小超上限截断为 maxLimit")
        void shouldTruncatePageSizeToMaxLimit() {
            Page<TestEntity> page = testMapper.selectPage(new Page<>(1, 10), null);

            Assertions.assertThat(page.getRecords()).hasSize(5);
        }

        @Test
        @DisplayName("当前页超出总页数时返回空")
        void shouldReturnEmptyWhenCurrentExceedsPages() {
            Page<TestEntity> page = testMapper.selectPage(new Page<>(100, 5), null);

            Assertions.assertThat(page.getTotal()).isEqualTo(10L);
            Assertions.assertThat(page.getRecords()).isEmpty();
        }

        @Test
        @DisplayName("无 WHERE 全表更新被拦截")
        void shouldBlockFullTableUpdate() {
            Assertions.assertThatThrownBy(() -> testMapper.update(null,
                            new LambdaUpdateWrapper<TestEntity>().set(TestEntity::getSomeField, "hacked")))
                    .isInstanceOf(MyBatisSystemException.class)
                    .hasRootCauseInstanceOf(MybatisPlusException.class);
        }

        @Test
        @DisplayName("软删除置位且查询过滤")
        void shouldSoftDeleteAndFilter() {
            TestEntity entity = examples.get(0);

            testMapper.deleteById(entity.getId());

            Assertions.assertThat(testMapper.selectById(entity.getId())).isNull();
            Assertions.assertThat(jdbcTemplate.queryForObject(
                    "SELECT delete_time FROM tests WHERE id = ?",
                    LocalDateTime.class, entity.getId())).isNotNull();
        }

        @Test
        @DisplayName("分页过滤已删行")
        void shouldExcludeDeletedRowsFromPagination() {
            examples.subList(0, 3).forEach(entity -> testMapper.deleteById(entity.getId()));

            Page<TestEntity> page = testMapper.selectPage(new Page<>(1, 2), null);

            Assertions.assertThat(page.getTotal()).isEqualTo(7L);
        }

        @Test
        @DisplayName("重复软删除返回 0")
        void shouldReturnZeroOnRepeatDelete() {
            TestEntity entity = examples.get(0);

            Assertions.assertThat(testMapper.deleteById(entity.getId())).isEqualTo(1);
            Assertions.assertThat(testMapper.deleteById(entity.getId())).isZero();
        }
    }

    @Nested
    @DisplayName("分页溢出")
    @TestPropertySource(properties = {
            "restproto.pagination.max-limit=5",
            "restproto.pagination.overflow=true"
    })
    class OverflowCases {

        @Autowired
        private TestMapper testMapper;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @BeforeEach
        void setUp() {
            // 重建测试表, 保证用例间数据独立
            jdbcTemplate.execute("DROP TABLE IF EXISTS tests");
            jdbcTemplate.execute("""
                    CREATE TABLE tests (
                        id BIGINT PRIMARY KEY,
                        some_field VARCHAR(64) NOT NULL,
                        create_time TIMESTAMP NULL,
                        update_time TIMESTAMP NULL,
                        delete_time TIMESTAMP NULL
                    )
                    """);

            List<TestEntity> examples = new ArrayList<>();
            IntStream.range(0, 10).forEach(idx -> {
                examples.add(TestEntity.builder()
                        .someField("test-" + idx)
                        .build());
            });
            testMapper.insert(examples);
        }

        @Test
        @DisplayName("当前页超出总页数时回退到第一页")
        void shouldOverflowToFirstPageWhenCurrentExceedsPages() {
            Page<TestEntity> page = testMapper.selectPage(new Page<>(100, 5), null);

            Assertions.assertThat(page.getTotal()).isEqualTo(10L);
            Assertions.assertThat(page.getCurrent()).isEqualTo(1L);
            Assertions.assertThat(page.getRecords()).hasSize(5);
        }
    }
}
