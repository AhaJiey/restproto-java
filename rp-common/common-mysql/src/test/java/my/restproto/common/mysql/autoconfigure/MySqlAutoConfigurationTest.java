package my.restproto.common.mysql.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.testcontainers.containers.MySQLContainer;

@DisplayName("mysql 自动配置测试")
class MySqlAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MySqlAutoConfiguration.class));

    @Test
    @DisplayName("默认装配分页与全表防护拦截器")
    void shouldRegisterPaginationAndBlockAttackInterceptors() {
        contextRunner.run(context -> {
            Assertions.assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
            MybatisPlusInterceptor interceptor = context.getBean(MybatisPlusInterceptor.class);

            Assertions.assertThat(interceptor.getInterceptors())
                    .hasSize(2)
                    .anyMatch(inner -> inner instanceof PaginationInnerInterceptor)
                    .anyMatch(inner -> inner instanceof BlockAttackInnerInterceptor);
        });
    }

    @Test
    @DisplayName("分页配置项反映到拦截器")
    void shouldApplyPaginationProperties() {
        contextRunner
                .withPropertyValues(
                        "restproto.pagination.max-limit=100",
                        "restproto.pagination.overflow=true"
                )
                .run(context -> {
                    PaginationInnerInterceptor pagination = paginationInterceptor(context);

                    Assertions.assertThat(pagination.getMaxLimit()).isEqualTo(100L);
                    Assertions.assertThat(pagination.isOverflow()).isTrue();
                    Assertions.assertThat(pagination.getDbType()).isEqualTo(DbType.MYSQL);
                });
    }

    @Test
    @DisplayName("max-limit 为 -1 时不限制")
    void shouldKeepUnlimitedWhenMaxLimitIsMinusOne() {
        contextRunner
                .withPropertyValues("restproto.pagination.max-limit=-1")
                .run(context ->
                        Assertions.assertThat(paginationInterceptor(context).getMaxLimit()).isEqualTo(-1L));
    }

    @Test
    @DisplayName("装配时间字段自动填充 handler")
    void shouldRegisterMetaObjectHandler() {
        contextRunner.run(context ->
                Assertions.assertThat(context).hasSingleBean(MetaObjectHandler.class));
    }

    @Test
    @DisplayName("懒狗容器默认不注册")
    void shouldNotRegisterLazydogContainerByDefault() {
        contextRunner.run(context ->
                Assertions.assertThat(context).doesNotHaveBean(MySQLContainer.class));
    }

    @Test
    @DisplayName("懒狗容器显式开启且未配数据源时注册")
    void shouldRegisterLazydogContainerWhenEnabled() {
        contextRunner
                .withPropertyValues("restproto.lazydog.mysql.enabled=true")
                .run(context -> {
                    Assertions.assertThat(context).hasSingleBean(MySQLContainer.class);
                    MySQLContainer<?> container = context.getBean(MySQLContainer.class);

                    Assertions.assertThat(container.getDatabaseName()).isEqualTo("lazydog");
                    Assertions.assertThat(container.getUsername()).isEqualTo("lazydog");
                });
    }

    @Test
    @DisplayName("已配数据源时懒狗容器退让")
    void shouldBackOffLazydogContainerWhenDatasourceConfigured() {
        contextRunner
                .withPropertyValues(
                        "restproto.lazydog.mysql.enabled=true",
                        "spring.datasource.url=jdbc:mysql://localhost:3306/db"
                )
                .run(context ->
                        Assertions.assertThat(context).doesNotHaveBean(MySQLContainer.class));
    }

    private PaginationInnerInterceptor paginationInterceptor(
            ApplicationContext context) {
        MybatisPlusInterceptor interceptor = context.getBean(MybatisPlusInterceptor.class);
        return interceptor.getInterceptors().stream()
                .filter(PaginationInnerInterceptor.class::isInstance)
                .map(PaginationInnerInterceptor.class::cast)
                .findFirst()
                .orElseThrow();
    }
}