package my.restproto.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 令牌携带配置项: 令牌所在请求头与携带模板
 */
@Data
@ConfigurationProperties(prefix = "restproto.token.header")
public class TokenHeaderProps {

    /** 携带模板中的令牌占位符, 固定值 */
    private static final String TOKEN_PLACE_HOLDER = "<token>";

    /** 令牌所在请求头 */
    private String name = "Authorization";

    /** 携带模板, 占位符位置即令牌串位置 */
    private String contentTemplate = "Bearer <token>";

    /** 取令牌占位符, 固定值不参与配置绑定 */
    public String getTokenPlaceHolder() {
        return TOKEN_PLACE_HOLDER;
    }
}
