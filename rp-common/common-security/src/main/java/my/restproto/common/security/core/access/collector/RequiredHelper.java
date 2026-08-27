package my.restproto.common.security.core.access.collector;

import my.restproto.common.security.core.access.annotations.NoRequired;
import my.restproto.common.security.core.access.annotations.RequireAuthenticated;
import my.restproto.common.security.core.access.annotations.RequirePermission;
import my.restproto.common.security.core.access.model.Rule;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;

/**
 * 访问注解归一, 把三种访问注解转为统一的访问要求串
 */
public class RequiredHelper {

    public static String getRequired(Annotation annotation) {
        if (annotation instanceof NoRequired) {
            return Rule.NO_REQUIRED;
        }
        if (annotation instanceof RequireAuthenticated) {
            return Rule.IS_AUTHENTICATED;
        }
        if (annotation instanceof RequirePermission permission) {
            String code = permission.permission();

            // 空白编码无法与任何令牌权限比对, 登记后等同于放行, 启动期即中断
            if (!StringUtils.hasText(code)) {
                throw new IllegalStateException("@RequirePermission 权限编码不得为空");
            }

            // 访问要求常量与权限编码共用一个字段, 占用会让权限规则被误判为无需要求
            if (code.equals(Rule.NO_REQUIRED) || code.equals(Rule.IS_AUTHENTICATED)) {
                throw new IllegalStateException("@RequirePermission 不得使用访问要求常量值");
            }

            return code;
        }
        throw new IllegalStateException("仅支持 @NoRequired, @RequireAuthenticated, @RequirePermission 注解");
    }
}
