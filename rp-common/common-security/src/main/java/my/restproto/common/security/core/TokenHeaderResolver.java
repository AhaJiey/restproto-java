package my.restproto.common.security.core;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import my.restproto.common.security.properties.TokenHeaderProps;
import org.springframework.util.StringUtils;

/**
 * 令牌头解析器, 按携带模板从请求头切出令牌串
 */
@RequiredArgsConstructor
public class TokenHeaderResolver {

    /** 令牌携带配置项 */
    private final TokenHeaderProps tokenHeaderProps;

    /** 从请求头解析令牌串, 头缺失或不符携带模板返回 null */
    public String resolveToken(HttpServletRequest request) {
        // 头缺失即视为未携带令牌
        String raw = request.getHeader(tokenHeaderProps.getName());
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String header = raw.trim();

        // 占位符把模板切成前后缀两段, 缺占位符说明模板本身配错
        String template = tokenHeaderProps.getContentTemplate();
        String placeHolder = tokenHeaderProps.getTokenPlaceHolder();
        int placeholderIndex = template.indexOf(placeHolder);
        if (placeholderIndex < 0) {
            return null;
        }
        String prefix = template.substring(0, placeholderIndex);
        String suffix = template.substring(placeholderIndex + placeHolder.length());

        // 前后缀对不上说明并非本服务约定的携带格式
        if (!header.startsWith(prefix) || !header.endsWith(suffix)) {
            return null;
        }

        // 前后缀长度之和超过头值长度时前后缀共用字符, 头值装不下完整模板, 视为格式不符
        if (header.length() < prefix.length() + suffix.length()) {
            return null;
        }

        // 去掉前后缀, 余下即令牌串
        String result = header.substring(prefix.length(), header.length() - suffix.length());
        return StringUtils.hasText(result) ? result : null;
    }
}
