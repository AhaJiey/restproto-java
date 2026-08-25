package my.restproto.common.security.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.restproto.common.security.core.access.AccessRuleSet;
import my.restproto.common.security.core.access.model.Endpoint;
import my.restproto.common.security.core.access.model.Rule;
import my.restproto.common.security.properties.AccessProps;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.security.Principal;

/**
 * URL 授权拦截器, 按端点规则判定放行与否
 */
@RequiredArgsConstructor
public class AccessDecideInterceptor implements HandlerInterceptor {

    /** 端点规则集 */
    private final AccessRuleSet accessRuleSet;

    /** 访问控制配置项 */
    private final AccessProps accessProps;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 仅接管控制器端点时, 静态资源等非控制器处理器直接放行
        boolean controllerOnly = accessProps.getScope() == AccessProps.Scope.CONTROLLER;
        if (controllerOnly && !(handler instanceof HandlerMethod)) {
            return true;
        }

        // 认证对象取一次, 供两轮判定共用
        Authentication authentication = getAuthentication(request);

        // 非标准方法同样无法命中规则, 交给规则判定按未登记端点拒绝
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        // 模板 URL 与真实 URI 各判一次, 任一通过即放行
        if (granted(
                authentication,
                Endpoint.builder()
                        .method(method)
                        // Spring MVC 匹配到的模板 URL
                        .url((String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
                        .build()
        )) {
            return true;
        }
        if (granted(
                authentication,
                Endpoint.builder()
                        .method(method)
                        // 携带的 URL
                        .url(request.getRequestURI())
                        .build()
        )) {
            return true;
        }

        // 拒绝一律抛出, 交由安全链按认证状态输出 401 或 403
        throw new AccessDeniedException("无权限访问");
    }

    /** 取当前认证对象, 未认证返回 null */
    private Authentication getAuthentication(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        // 匿名与 principal 为空均已被 SecurityContextHolderAwareRequestWrapper 归零
        if (principal == null) {
            return null;
        }

        // 非认证对象说明并非本安全链写入, 不予采信
        if (!(principal instanceof Authentication authentication)) {
            return null;
        }

        // 未认证的令牌在此挡下
        if (!authentication.isAuthenticated()) {
            return null;
        }

        return authentication;
    }

    /** 按端点规则判定给定认证对象能否访问该端点 */
    private boolean granted(Authentication authentication, Endpoint endpoint) {
        // 未登记即拒绝, 即默认全部拒绝
        Rule rule = accessRuleSet.get(endpoint);
        if (rule == null) {
            return false;
        }

        // 无需任何要求的端点直接放行
        if (rule.noRequired()) {
            return true;
        }

        // 其余访问要求均以已认证为前提
        if (authentication == null) {
            return false;
        }

        // 仅要求已认证的端点到此放行
        if (rule.isAuthenticated()) {
            return true;
        }

        // 比对令牌权限集是否满足规则的访问要求
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> rule.match(authority.getAuthority()));
    }
}
