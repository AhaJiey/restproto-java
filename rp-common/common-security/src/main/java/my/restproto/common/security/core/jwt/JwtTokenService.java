package my.restproto.common.security.core.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import my.restproto.common.redis.RedisOps;
import my.restproto.common.security.core.jwt.model.JwtTokenClaims;
import my.restproto.common.security.core.jwt.model.JwtTokenPair;
import my.restproto.common.security.properties.JwtTokenIssueProps;
import org.springframework.security.authentication.BadCredentialsException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 令牌服务, 基于 jjwt 签发与解析, 撤销走 redis 黑名单
 */
@RequiredArgsConstructor
public class JwtTokenService {

    /** 签发配置项 */
    private final JwtTokenIssueProps issueProperties;

    /** 撤销黑名单存储 */
    private final RedisOps redisOps;

    /** JSON 转换器, 完成 claims 与实体双向映射 */
    private final ObjectMapper objectMapper;

    /** 签名密钥缓存 */
    private SecretKey secretKey;

    /** 按配置密钥构造签名密钥, 懒加载缓存 */
    private SecretKey key() {
        if (secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(
                    issueProperties.getSecret().getBytes(StandardCharsets.UTF_8)
            );
        }
        return secretKey;
    }

    /** 未勾选记住我时的会话时长 */
    public Duration getSessionValidDurationWithoutRememberMe() {
        return issueProperties.getRtValidDurationWithoutRememberMe();
    }

    /** 勾选记住我时的会话时长 */
    public Duration getTokenValidDurationWithRememberMe() {
        return issueProperties.getRtValidDurationWithRememberMe();
    }

    /** 签发双令牌 */
    public JwtTokenPair issue(String identity, JwtTokenClaims.Extended extended) {
        Instant now = Instant.now();

        Instant atExpiredAt = now.plus(issueProperties.getAtValidDuration());
        Instant sessionExpiredAt = extended.getSessionExpiration();

        // 刷新令牌与会话以该时刻为存活上界, 缺失时按默认会话时长兜底, 避免产出永不过期的令牌
        if (sessionExpiredAt == null) {
            sessionExpiredAt = now.plus(getSessionValidDurationWithoutRememberMe());
        }

        // 会话标识由调用方给定则沿用, 未给定则新开一个会话
        String sessionId = extended.getSessionId() == null
                ? UUID.randomUUID().toString()
                : extended.getSessionId();
        String atId = UUID.randomUUID().toString();
        String rtId = UUID.randomUUID().toString();

        // 组装访问令牌声明, 与刷新令牌同属一个会话
        JwtTokenClaims atClaims = JwtTokenClaims.builder()
                .standard(JwtTokenClaims.Standard.builder()
                        .subject(identity)
                        .tokenId(atId)
                        .issuedAt(now)
                        .expiration(atExpiredAt)
                        .build())
                .extended(JwtTokenClaims.Extended.builder()
                        .sessionId(sessionId)
                        .sessionExpiration(sessionExpiredAt)
                        .permissions(extended.getPermissions())
                        .build())
                .build();

        // 组装刷新令牌声明, 有效期与会话上限同止, 不携带权限
        JwtTokenClaims rtClaims = JwtTokenClaims.builder()
                .standard(JwtTokenClaims.Standard.builder()
                        .subject(identity)
                        .tokenId(rtId)
                        .issuedAt(now)
                        .expiration(sessionExpiredAt)
                        .build())
                .extended(JwtTokenClaims.Extended.builder()
                        .sessionId(sessionId)
                        .sessionExpiration(sessionExpiredAt)
                        .build())
                .build();

        return JwtTokenPair.builder()
                .at(compact(atClaims))
                .rt(compact(rtClaims))
                .atClaims(atClaims)
                .rtClaims(rtClaims)
                .build();
    }

    /** 解析令牌, 校验签名、过期与会话上限, 失败统一抛认证异常 */
    public JwtTokenClaims parse(String token) {
        Instant now = Instant.now();
        try {
            Map<String, Object> payload = Jwts.parser()
                    .clock(() -> Date.from(now))
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 声明聚合映射为实体, 时间字段由 jjwt 的 Date 经字段绑定还原为 Instant
            JwtTokenClaims jwtTokenClaims = objectMapper.convertValue(payload, JwtTokenClaims.class);
            JwtTokenClaims.Standard standard = jwtTokenClaims.getStandard();
            JwtTokenClaims.Extended extended = jwtTokenClaims.getExtended();

            // 标准声明或扩展声明整体缺失都不符合本服务约定, 按认证失败处理
            if (standard == null || extended == null) {
                throw new JwtException("令牌声明缺失");
            }

            // 会话到期即令牌失效, 刷新不延长该时刻, 到顶后无法再续
            Instant sessionExpiration = extended.getSessionExpiration();
            if (sessionExpiration != null && !sessionExpiration.isAfter(now)) {
                throw new JwtException("令牌过期");
            }

            if (extended.getPermissions() == null) {
                extended.setPermissions(List.of());
            }
            return jwtTokenClaims;
        } catch (JwtException ex) {
            throw new BadCredentialsException("令牌无效或已过期");
        }
    }

    /** 撤销令牌, 连同其所属会话一并撤销 */
    public void revoke(String token) {
        revoke(token, true);
    }

    /** 撤销令牌, 会话级撤销由调用方控制, 令牌条目按自身剩余, 会话条目按会话剩余 */
    public void revoke(String token, boolean revokeSession) {
        JwtTokenClaims claims;
        try {
            claims = parse(token);
        } catch (BadCredentialsException ignored) {
            // 无效或过期令牌无剩余有效期, 不写黑名单
            return;
        }

        // 令牌条目按自身剩余有效期写入, 只封这一个令牌
        Instant now = Instant.now();
        writeRevokedIfPositive(claims.getStandard().getTokenId(),
                Duration.between(now, claims.getStandard().getExpiration()));

        // 会话条目按会话剩余写入, 会话是任何令牌的存活上界, 一并封掉同会话的其余令牌;
        // 会话过期时刻缺失时无法确定存活上界, 该支跳过
        if (revokeSession && claims.getExtended().getSessionExpiration() != null) {
            writeRevokedIfPositive(claims.getExtended().getSessionId(),
                    Duration.between(now, claims.getExtended().getSessionExpiration()));
        }
    }

    /** 剩余时长非正即跳过写入, 已失效的令牌与会话无需封禁 */
    private void writeRevokedIfPositive(String key, Duration duration) {
        if (!duration.isNegative() && !duration.isZero()) {
            redisOps.set(key, "revoked", new TypeReference<String>() {}, duration);
        }
    }

    /** 令牌是否已撤销, 会话与令牌任一入黑名单即视为撤销 */
    public boolean isRevoked(JwtTokenClaims claims) {
        return redisOps.get(claims.getExtended().getSessionId(), new TypeReference<String>() {}) != null
                || redisOps.get(claims.getStandard().getTokenId(), new TypeReference<String>() {}) != null;
    }

    /** 将声明聚合转为 claims map 写入 jjwt, 时间字段经 NUMBER 形态序列化输出 epoch 秒 */
    private String compact(JwtTokenClaims jwtTokenClaims) {
        Map<String, Object> claims = objectMapper.convertValue(
                jwtTokenClaims, new TypeReference<Map<String, Object>>() {});
        return Jwts.builder()
                .claims(claims)
                .signWith(key())
                .compact();
    }
}
