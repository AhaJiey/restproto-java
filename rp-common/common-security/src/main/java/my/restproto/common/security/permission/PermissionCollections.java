package my.restproto.common.security.permission;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限注册表, 维护代码中全部 Permission 权限标识
 */
public class PermissionCollections {

    private final Set<String> permissions = ConcurrentHashMap.newKeySet();

    /** 注册单个权限 */
    public void register(String permission) {
        permissions.add(permission);
    }

    /** 批量注册权限 */
    public void registerAll(Collection<String> permissions) {
        this.permissions.addAll(permissions);
    }

    /** 移除单个权限 */
    public void remove(String permission) {
        permissions.remove(permission);
    }

    /** 检查权限是否已注册 */
    public boolean contains(String permission) {
        return permissions.contains(permission);
    }

    /** 返回权限快照 */
    public Set<String> list() {
        return Set.copyOf(permissions);
    }
}
