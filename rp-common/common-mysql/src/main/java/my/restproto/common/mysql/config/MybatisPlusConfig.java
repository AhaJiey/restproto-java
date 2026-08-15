package my.restproto.common.mysql.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import my.restproto.common.mysql.properties.PaginationProperties;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * MyBatis-Plus 配置
 */
@Configuration
public class MybatisPlusConfig {

    /** 分页与全表操作防护拦截器, 消费者自定义时让位 */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(PaginationProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 构造分页拦截器并应用配置项
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(properties.getMaxLimit());
        pagination.setOverflow(properties.isOverflow());
        interceptor.addInnerInterceptor(pagination);

        // 阻止无 WHERE 的全表更新或删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

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
                // 插入时填充创建时间与更新时间
                this.strictInsertFill(metaObject, createTimeField, LocalDateTime.class, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                this.strictInsertFill(metaObject, updateTimeField, LocalDateTime.class, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 更新时刷新更新时间
                this.strictUpdateFill(metaObject, updateTimeField, LocalDateTime.class, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            }
        };
    }
}
