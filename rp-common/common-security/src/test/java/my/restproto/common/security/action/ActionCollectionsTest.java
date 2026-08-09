package my.restproto.common.security.action;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 操作注册表单元测试
 */
class ActionCollectionsTest {

    private final ActionCollections collections = new ActionCollections();

    /** 注册后 contains 为 true 且 list 包含 */
    @Test
    void register() {
        collections.register("user:read");
        assertTrue(collections.contains("user:read"));
        assertTrue(collections.list().contains("user:read"));
    }

    /** 批量注册 */
    @Test
    void registerAll() {
        collections.registerAll(List.of("user:read", "user:write"));
        assertTrue(collections.contains("user:read"));
        assertTrue(collections.contains("user:write"));
    }

    /** 重复注册去重 */
    @Test
    void registerDeduplicate() {
        collections.register("user:read");
        collections.register("user:read");
        assertEquals(1, collections.list().size());
    }

    /** 移除后 contains 为 false */
    @Test
    void remove() {
        collections.register("user:read");
        collections.remove("user:read");
        assertFalse(collections.contains("user:read"));
    }

    /** list 返回快照不可变 */
    @Test
    void listSnapshot() {
        collections.register("user:read");
        var snapshot = collections.list();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add("user:write"));
    }
}
