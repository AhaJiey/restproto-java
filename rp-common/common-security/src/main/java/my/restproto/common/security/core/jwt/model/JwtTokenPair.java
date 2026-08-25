package my.restproto.common.security.core.jwt.model;

import lombok.Builder;
import lombok.Data;

/**
 * 令牌签发结果, 双令牌串与各自声明, 两者同属一个会话
 */
@Data
@Builder
public class JwtTokenPair {

    /** 访问令牌串 */
    private String at;

    /** 刷新令牌串 */
    private String rt;

    /** 访问令牌声明 */
    private JwtTokenClaims atClaims;

    /** 刷新令牌声明 */
    private JwtTokenClaims rtClaims;
}
