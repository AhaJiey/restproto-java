package my.restproto.common.security.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.restproto.common.security.properties.JwtTokenIssueProps;
import my.restproto.common.security.properties.LazydogJwtProps;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 懒狗密钥, 显式开启且未配restproto.token.issue.secret才注册
 */
@Slf4j
@ConditionalOnProperty(prefix = "restproto.lazydog.jwt", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class LazydogJwtSecretConfig {

    private final LazydogJwtProps lazydogJwtProps;

    private final JwtTokenIssueProps jwtTokenIssueProps;

    @PostConstruct
    public void process() {
        // 真实密钥已配置时让位, 懒狗只在空位上兜底, 不顶掉线上配置
        if (StringUtils.hasText(jwtTokenIssueProps.getSecret())) {
            log.warn("配置了 restproto.lazydog.jwt.enabled = true, 但也配置了 restproto.token.issue, 懒狗配置将不再生效");
            return;
        }

        // 留空与 auto 同义, 开关一开即给一把随机密钥, 其余值当固定密钥直接用
        String configured = lazydogJwtProps.getSecret();
        String secret = !StringUtils.hasText(configured) || LazydogJwtProps.AUTO.equals(configured)
                ? randomSecret()
                : configured;

        // 补齐签发密钥, 令牌服务首次取用时拿到的即此处写入的值
        jwtTokenIssueProps.setSecret(secret);

        // 密钥出全文而非掩码, 联调本就不忌泄漏, 出全文才能原样粘回配置固定下来
        log.warn("懒狗密钥已启用, 本次密钥 {}, 勿用于生产", secret);
    }

    /**
     * 生成随机密钥
     */
    private String randomSecret() {
        byte[] bytes = new byte[lazydogJwtProps.getSecretLength()];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
