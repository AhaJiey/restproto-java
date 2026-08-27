package my.restproto.common.migration.core;

import my.restproto.common.migration.FlywayMigration;
import my.restproto.common.migration.properties.FlywayMigrationProps;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * 解析 {@link FlywayMigration} 注解并注册按模块隔离的 Flyway Bean
 */
public class FlywayMigrationRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry,
            BeanNameGenerator importBeanNameGenerator
    ) {
        // 解析注解属性, module 与 name 互为别名
        FlywayMigration annotation = importingClassMetadata.getAnnotations()
                .get(FlywayMigration.class)
                .synthesize();
        String module = annotation.module();

        if (!StringUtils.hasText(module)) {
            throw new IllegalStateException("@FlywayMigration 必须指定 module 或 name");
        }

        String flywayBeanName = module + "Flyway";
        // 防止同一模块重复注册
        if (registry.containsBeanDefinition(flywayBeanName)) {
            throw new IllegalStateException("@FlywayMigration 模块 " + module + " 已注册迁移");
        }

        FlywayMigrationProps config = FlywayMigrationProps.builder()
                .module(module)
                .location(annotation.location())
                .table(annotation.table())
                .baselineVersion(annotation.baselineVersion())
                .failOnMissingLocations(annotation.failOnMissingLocations())
                .encoding(annotation.encoding())
                .build();
        BeanDefinitionBuilder flywayDefinition = BeanDefinitionBuilder.genericBeanDefinition(FlywayFactoryBean.class)
                .addConstructorArgValue(config)
                .addConstructorArgReference(StringUtils.uncapitalize(DataSource.class.getSimpleName()));

        registry.registerBeanDefinition(flywayBeanName, flywayDefinition.getBeanDefinition());

        // 注册迁移执行 Bean, 启动时执行模块迁移
        BeanDefinitionBuilder flywayMigrationDefinition = BeanDefinitionBuilder.genericBeanDefinition(FlywayMigrationInitializer.class)
                .addConstructorArgReference(flywayBeanName);

        registry.registerBeanDefinition(module + "FlywayMigrationInitializer", flywayMigrationDefinition.getBeanDefinition());
    }
}
