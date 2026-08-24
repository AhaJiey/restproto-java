package my.restproto.common.migration.core;

import my.restproto.common.migration.FlywayMigration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

@DisplayName("FlywayMigrationRegistrar 注册逻辑测试")
class FlywayMigrationRegistrarTest {

    @Test
    @DisplayName("module 属性注册独立迁移 bean")
    void shouldRegisterBeansByModule() {
        BeanDefinitionRegistry registry = processConfigs(ModuleAConfig.class);

        Assertions.assertThat(registry.containsBeanDefinition("module-aFlyway")).isTrue();
        Assertions.assertThat(registry.containsBeanDefinition("module-aFlywayMigrationInitializer")).isTrue();
    }

    @Test
    @DisplayName("name 别名与 module 等价注册")
    void shouldRegisterBeansByNameAlias() {
        BeanDefinitionRegistry registry = processConfigs(ModuleANameConfig.class);

        Assertions.assertThat(registry.containsBeanDefinition("module-aFlyway")).isTrue();
        Assertions.assertThat(registry.containsBeanDefinition("module-aFlywayMigrationInitializer")).isTrue();
    }

    @Test
    @DisplayName("module 与 name 均未指定时拒绝注册")
    void shouldFailWhenModuleMissing() {
        Assertions.assertThatThrownBy(() -> processConfigs(EmptyModuleConfig.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("必须指定 module 或 name");
    }

    @Test
    @DisplayName("同一模块重复注册时拒绝")
    void shouldFailOnDuplicateModule() {
        Assertions.assertThatThrownBy(() -> processConfigs(ModuleAConfig.class, ModuleADuplicateConfig.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("已注册迁移");
    }

    /**
     * 仅解析配置类的注册逻辑, 不启动容器, 避免触发真实迁移
     */
    private BeanDefinitionRegistry processConfigs(Class<?>... configClasses) {
        GenericApplicationContext context = new GenericApplicationContext();
        for (Class<?> configClass : configClasses) {
            context.registerBeanDefinition(
                    configClass.getName(), new AnnotatedGenericBeanDefinition(configClass));
        }

        ConfigurationClassPostProcessor processor = new ConfigurationClassPostProcessor();
        processor.setEnvironment(new StandardEnvironment());
        processor.postProcessBeanDefinitionRegistry(context);
        return context;
    }

    @Configuration
    @FlywayMigration(module = "module-a")
    static class ModuleAConfig {
    }

    @Configuration
    @FlywayMigration(module = "module-a")
    static class ModuleADuplicateConfig {
    }

    @Configuration
    @FlywayMigration(name = "module-a")
    static class ModuleANameConfig {
    }

    @Configuration
    @FlywayMigration
    static class EmptyModuleConfig {
    }
}
