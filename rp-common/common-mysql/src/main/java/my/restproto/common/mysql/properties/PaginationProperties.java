package my.restproto.common.mysql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * mysql 领域配置项, 控制分页拦截器行为
 */
@Data
@ConfigurationProperties(prefix = "restproto.pagination")
public class PaginationProperties {

    /** 分页单页上限, -1 表示不限制 */
    private long maxLimit = 500L;

    /** 页大小超出上限时是否溢出到最后一页 */
    private boolean overflow = false;
}
