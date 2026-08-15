package my.restproto.common.security.autoconfigure;

import my.restproto.common.restful.autoconfigure.RestfulAutoConfiguration;
import my.restproto.common.restful.tools.ResponseWriter;
import my.restproto.common.security.SecurityConfig;
import my.restproto.common.security.permission.PermissionAspect;
import my.restproto.common.security.permission.PermissionCollections;
import my.restproto.common.security.exception.AuthExceptionHandler;
import my.restproto.common.security.exception.DenyHandler;
import my.restproto.common.security.exception.UnAuthHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * security 领域自动配置, 注册无状态安全链与注解授权组件
 */
@AutoConfiguration
@AutoConfigureAfter(RestfulAutoConfiguration.class)
@Import(SecurityConfig.class)
public class SecurityAutoConfiguration {

    /** 权限注册表 */
    @Bean
    public PermissionCollections permissionCollections() {
        return new PermissionCollections();
    }

    /** 未认证入口 */
    @Bean
    public UnAuthHandler unAuthHandler(ResponseWriter responseWriter) {
        return new UnAuthHandler(responseWriter);
    }

    /** 权限拒绝处理器 */
    @Bean
    public DenyHandler denyHandler(ResponseWriter responseWriter) {
        return new DenyHandler(responseWriter);
    }

    /** auth 异常处理 */
    @Bean
    public AuthExceptionHandler authExceptionHandler() {
        return new AuthExceptionHandler();
    }

    /** Permission 动态权限切面 */
    @Bean
    public PermissionAspect permissionAspect() {
        return new PermissionAspect();
    }
}
