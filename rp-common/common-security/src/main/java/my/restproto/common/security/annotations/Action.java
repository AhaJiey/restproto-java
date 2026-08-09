package my.restproto.common.security.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法级操作注解, value 即所需权限标识, 由 ActionAspect 在方法执行前校验
 * 建议命名 {resource}:{action}, 如 user:create
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Action {

    /** 操作对应权限标识, 校验当前用户 authorities 是否包含该值 */
    String value();
}
