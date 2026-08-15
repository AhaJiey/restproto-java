package my.restproto.common.security.permission;

import my.restproto.common.security.annotations.Permission;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Permission 动态权限切面, 方法执行前校验当前用户 authorities 是否包含对应权限
 */
@Aspect
public class PermissionAspect {

    /** 拦截标注 Permission 的方法, 无对应权限则抛 AccessDeniedException 交由安全链处理 */
    @Before("@annotation(permission)")
    public void checkAuthority(JoinPoint joinPoint, Permission permission) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("未认证或认证已过期");
        }

        boolean authorized = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals(permission.value()));

        if (!authorized) {
            throw new AccessDeniedException("无权限访问");
        }
    }
}
