package my.restproto.common.security.annotations;

import my.restproto.common.security.action.ActionScanRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 仿照 ComponentScan, 在启动类标注并指定 Action 注解扫描包,
 * 由 ActionScanRegistrar 注册 ActionCollector 并注入扫描路径
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ActionScanRegistrar.class)
public @interface ActionScan {

    /** 扫描包路径, 与 basePackages 互为别名 */
    @AliasFor("basePackages")
    String[] value() default {};

    /** 扫描包路径, 与 value 互为别名 */
    @AliasFor("value")
    String[] basePackages() default {};
}
