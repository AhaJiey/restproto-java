package my.restproto.common.security.core.access.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

/**
 * 端点访问规则, 一条规则对应一个 HTTP 方法与一个模板 URL
 */
@Data
@Builder
public class Rule {

    /** 无需任何要求 */
    public static final String NO_REQUIRED = "noRequired";

    /** 仅要求已认证 */
    public static final String IS_AUTHENTICATED = "isAuthenticated";

    /** 端点标识, HTTP 方法与模板 URL */
    private Endpoint endpoint;

    /** 访问要求, 取两个常量之一, 否则即为权限编码 */
    private String required;

    /** 是否无需任何要求 */
    @JsonIgnore
    public boolean noRequired() {
        return NO_REQUIRED.equals(required);
    }

    /** 是否仅要求已认证 */
    @JsonIgnore
    public boolean isAuthenticated() {
        return IS_AUTHENTICATED.equals(required);
    }

    /** 给定权限编码是否满足本规则的访问要求 */
    public boolean match(String code) {
        return required != null && required.equals(code);
    }
}
