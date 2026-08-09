package my.restproto.common.security.action;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 操作注册表, 维护代码中全部 Action 权限标识
 */
@Component
public class ActionCollections {

    private final Set<String> actions = ConcurrentHashMap.newKeySet();

    /** 注册单个操作 */
    public void register(String action) {
        actions.add(action);
    }

    /** 批量注册操作 */
    public void registerAll(Collection<String> actions) {
        this.actions.addAll(actions);
    }

    /** 移除单个操作 */
    public void remove(String action) {
        actions.remove(action);
    }

    /** 检查操作是否已注册 */
    public boolean contains(String action) {
        return actions.contains(action);
    }

    /** 返回操作快照 */
    public Set<String> list() {
        return Set.copyOf(actions);
    }
}
