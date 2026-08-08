package my.restproto.common.restful.security.action;

import my.restproto.common.restful.security.annotations.Action;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Action 动态权限切面, 方法执行前校验当前用户 authorities 是否包含操作权限
 */
@Aspect
@Component
public class ActionAspect {

    /** 拦截标注 Action 的方法, 无对应权限则抛 AccessDeniedException 交由安全链处理 */
    @Before("@annotation(action)")
    public void checkAuthority(JoinPoint joinPoint, Action action) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("未认证或认证已过期");
        }

        boolean authorized = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals(action.value()));

        if (!authorized) {
            throw new AccessDeniedException("无权限访问");
        }
    }
}
