package my.restproto.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 访问控制配置项: 授权拦截器的接管范围
 */
@Data
@ConfigurationProperties(prefix = "restproto.access")
public class AccessProps {

    /** 接管范围 */
    private Scope scope = Scope.CONTROLLER;

    /**
     * 授权拦截器的接管范围
     */
    public enum Scope {

        /** 仅接管控制器端点, 静态资源等处理器直接放行 */
        CONTROLLER,

        /** 接管全部端点, 静态资源等处理器一并纳入判定 */
        ALL
    }
}
