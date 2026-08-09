package my.restproto.common.security.action;

import my.restproto.common.security.TestApplication;
import my.restproto.common.security.annotations.ActionScan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Action 启动扫描注册测试
 */
@SpringBootTest(classes = {TestApplication.class, ActionCollectorTest.ScanConfig.class})
class ActionCollectorTest {

    /** 测试配置, 指定 Action 扫描包 */
    @Configuration
    @ActionScan("my.restproto.common.security.action.support")
    static class ScanConfig {
    }

    @Autowired
    private ActionCollections collections;

    /** 启动后注册表包含扫描到的方法级 action */
    @Test
    void collectActions() {
        assertTrue(collections.contains("user:read"));
        assertTrue(collections.contains("user:write"));
    }

    /** 无注解方法不产生注册条目 */
    @Test
    void noActionMethodNotRegistered() {
        assertTrue(collections.list().stream().noneMatch(s -> s.equals("user:plain")));
    }
}
