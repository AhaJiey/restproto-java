package my.restproto.common.security.permission;

import my.restproto.common.security.TestApplication;
import my.restproto.common.security.annotations.PermissionScan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Permission 启动扫描注册测试
 */
@SpringBootTest(classes = {TestApplication.class, PermissionCollectorTest.ScanConfig.class})
class PermissionCollectorTest {

    /** 测试配置, 指定 Permission 扫描包 */
    @Configuration
    @PermissionScan("my.restproto.common.security.permission.support")
    static class ScanConfig {
    }

    @Autowired
    private PermissionCollections collections;

    /** 启动后注册表包含扫描到的方法级权限 */
    @Test
    void collectPermissions() {
        assertTrue(collections.contains("user:read"));
        assertTrue(collections.contains("user:write"));
    }

    /** 无注解方法不产生注册条目 */
    @Test
    void noPermissionMethodNotRegistered() {
        assertTrue(collections.list().stream().noneMatch(s -> s.equals("user:plain")));
    }
}
