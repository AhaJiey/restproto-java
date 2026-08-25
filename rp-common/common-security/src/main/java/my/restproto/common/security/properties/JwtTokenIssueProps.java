package my.restproto.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 令牌签发配置项: 双令牌有效期与签名密钥
 */
@Data
@ConfigurationProperties(prefix = "restproto.token.issue.jwt")
public class JwtTokenIssueProps {

    /** 访问令牌有效期 */
    private Duration atValidDuration = Duration.ofMinutes(30);

    /** 刷新令牌有效期 */
    private Duration rtValidDurationWithoutRememberMe = Duration.ofDays(7);

    /** 当记住我时, 刷新令牌有效时长 */
    private Duration rtValidDurationWithRememberMe = Duration.ofDays(30);

    /** 签名密钥 */
    private String secret = "";
}
