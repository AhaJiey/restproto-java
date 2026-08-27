package my.restproto.common.security.core.jwt.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * 令牌声明聚合, 以 jjwt 标准字段名作为 JSON 字段名
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenClaims {

    /** 标准声明, 对应 jwt 规范字段 */
    @JsonUnwrapped
    private Standard standard;

    /** 扩展声明, 会话与权限等自定义字段 */
    @JsonUnwrapped
    private Extended extended;

    /**
     * 标准声明, 字段名取 jjwt 常量, 与 jwt 规范一一对应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Standard {

        /** 身份标识, 标准 claim sub */
        @JsonProperty(Claims.SUBJECT)
        private String subject;

        /** 签发者, 标准 claim iss */
        @JsonProperty(Claims.ISSUER)
        private String issuer;

        /** 受众, 标准 claim aud */
        @JsonProperty(Claims.AUDIENCE)
        private String audience;

        /** 令牌 id, 标准 claim jti */
        @JsonProperty(Claims.ID)
        private String tokenId;

        /** 过期时间, 标准 claim exp */
        @JsonProperty(Claims.EXPIRATION)
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Instant expiration;

        /** 签发时间, 标准 claim iat */
        @JsonProperty(Claims.ISSUED_AT)
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Instant issuedAt;

        /** 生效时间, 标准 claim nbf */
        @JsonProperty(Claims.NOT_BEFORE)
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Instant notBefore;
    }

    /**
     * 扩展声明, 会话归属与权限随令牌一同下发
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Extended {

        /** 会话标识, 同一会话签发的令牌共享该值, 自定义 claim */
        private String sessionId;

        /** 会话过期时间, 首次签发时定下, 刷新不予延长, 自定义 claim */
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Instant sessionExpiration;

        /** 权限编码列表, 自定义 claim, 默认空列表 */
        private List<String> permissions;
    }
}
