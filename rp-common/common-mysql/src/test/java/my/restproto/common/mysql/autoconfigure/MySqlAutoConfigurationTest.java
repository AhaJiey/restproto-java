package my.restproto.common.mysql.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * mysql 自动配置测试: 分页拦截器注册与配置项应用
 */
class MySqlAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(MySqlAutoConfiguration.class));

    /** 默认注册分页与全表防护拦截器 */
    @Test
    void paginationInterceptorRegistered() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
            MybatisPlusInterceptor interceptor = context.getBean(MybatisPlusInterceptor.class);

            assertThat(interceptor.getInterceptors())
                    .hasSize(2)
                    .anyMatch(inner -> inner instanceof PaginationInnerInterceptor)
                    .anyMatch(inner -> inner instanceof BlockAttackInnerInterceptor);
        });
    }

    /** 配置项反映到分页拦截器 */
    @Test
    void paginationPropertiesApplied() {
        contextRunner
                .withPropertyValues(
                        "restproto.pagination.max-limit=100",
                        "restproto.pagination.overflow=true"
                )
                .run(context -> {

                    MybatisPlusInterceptor interceptor = context.getBean(MybatisPlusInterceptor.class);
                    PaginationInnerInterceptor pagination =
                            (PaginationInnerInterceptor) interceptor.getInterceptors().get(0);

                    assertThat(pagination.getMaxLimit()).isEqualTo(100L);
                    assertThat(pagination.isOverflow()).isTrue();
                    assertThat(pagination.getDbType()).isEqualTo(DbType.MYSQL);
                });
    }

    /** 消费者自定义拦截器时模块让位 */
    @Test
    void customInterceptorBacksOff() {
        contextRunner
                .withBean("customInterceptor", MybatisPlusInterceptor.class)
                .run(context ->
                        assertThat(context).hasSingleBean(MybatisPlusInterceptor.class));
    }
}
