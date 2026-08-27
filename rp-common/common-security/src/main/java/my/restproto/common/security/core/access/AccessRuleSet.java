package my.restproto.common.security.core.access;

import my.restproto.common.security.core.access.model.Endpoint;
import my.restproto.common.security.core.access.model.Rule;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 端点规则集, 按端点持有全部访问要求, 请求期查此表判定放行
 */
public class AccessRuleSet {

    /** 端点到规则的映射, 请求期按端点直接命中 */
    private final Map<Endpoint, Rule> rules = new ConcurrentHashMap<>();

    /** 登记规则, 同一端点重复登记时后者覆盖前者 */
    public void add(Rule... rules) {
        for (Rule rule : rules) {
            // 不限方法的端点定位不到唯一规则, 一律不予登记
            if (rule.getEndpoint().getMethod() == null) {
                throw new IllegalStateException("端点 " + rule.getEndpoint().getUrl() + " 未限定 HTTP 方法");
            }

            this.rules.put(rule.getEndpoint(), rule);
        }
    }

    /** 移除规则 */
    public void remove(Rule... rules) {
        for (Rule rule : rules) {
            this.rules.remove(rule.getEndpoint());
        }
    }

    /** 按 HTTP 方法名与模板 URL 取规则 */
    public Rule get(String method, String url) {
        return get(HttpMethod.valueOf(method), url);
    }

    /** 按 HTTP 方法与模板 URL 取规则, 未登记返回 null */
    public Rule get(HttpMethod method, String url) {
        Endpoint endpoint = Endpoint.builder()
                .method(method)
                .url(url)
                .build();
        return get(endpoint);
    }

    /** 按端点取规则, 未登记返回 null */
    public Rule get(Endpoint endpoint) {
        return rules.get(endpoint);
    }
}
