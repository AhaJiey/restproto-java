package my.restproto.common.mysql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * mysql 领域配置项, 控制分页拦截器行为
 */
@Data
@ConfigurationProperties(prefix = "restproto.pagination")
public class PaginationProps {

    /** 分页单页上限, -1 表示不限制 */
    private long maxLimit = 500L;

    /** 当前页超出总页数时是否回退到第一页 */
    private boolean overflow = false;
}
