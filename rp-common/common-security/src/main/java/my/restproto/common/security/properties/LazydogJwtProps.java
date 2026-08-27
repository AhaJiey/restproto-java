package my.restproto.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 懒狗密钥配置项: 联调期的 JWT 签名密钥, 默认关闭, 仅在签发密钥空缺时兜底
 */
@Data
@ConfigurationProperties(prefix = "restproto.lazydog.jwt")
public class LazydogJwtProps {

    /** 随机密钥标识, 取该值时每次启动换一把新密钥 */
    public static final String AUTO = "auto";

    /** 是否启用懒狗密钥 */
    private boolean enabled = false;

    /** 联调密钥, auto 为随机生成, 其余值当固定密钥直接用 */
    private String secret = AUTO;

    private Integer secretLength = 32;
}
