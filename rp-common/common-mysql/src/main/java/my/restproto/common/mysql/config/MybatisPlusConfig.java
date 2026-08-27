package my.restproto.common.mysql.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import my.restproto.common.mysql.properties.PaginationProps;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 装配分页与全表防护拦截器、时间字段自动填充
 */
@EnableConfigurationProperties({
        PaginationProps.class
})
@Configuration
public class MybatisPlusConfig {

    /** 分页与全表操作防护拦截器 */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(PaginationProps properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 阻止无 WHERE 的全表更新或删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(properties.getMaxLimit());
        pagination.setOverflow(properties.isOverflow());
        interceptor.addInnerInterceptor(pagination);

        return interceptor;
    }

    /** 实体时间字段自动填充 */
    @Bean
    public MetaObjectHandler mybatisPlusMetaObjectHandler() {
        final String createTimeField = "createTime";
        final String updateTimeField = "updateTime";
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                // 同一次插入的两个时间取同一时刻
                Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                this.strictInsertFill(metaObject, createTimeField, Instant.class, now);
                this.strictInsertFill(metaObject, updateTimeField, Instant.class, now);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, updateTimeField, Instant.class,
                        Instant.now().truncatedTo(ChronoUnit.SECONDS));
            }
        };
    }
}
