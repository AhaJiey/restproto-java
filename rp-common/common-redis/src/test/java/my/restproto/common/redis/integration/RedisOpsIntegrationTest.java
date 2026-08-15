package my.restproto.common.redis.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import my.restproto.common.redis.RedisOps;
import my.restproto.common.redis.integration.support.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * RedisOps 集成测试: 存取、类型保留与自然过期
 */
@SpringBootTest
class RedisOpsIntegrationTest {

    @Autowired
    private RedisOps redisOps;

    /** 简单类型存取往返一致 */
    @Test
    void simpleTypeRoundTrip() {
        String key = "simple-key";
        redisOps.set(key, "hello", new TypeReference<String>() {}, Duration.ofMinutes(5));

        assertThat(redisOps.<String>get(key)).isEqualTo("hello");
    }

    /** POJO 存取往返一致 */
    @Test
    void pojoRoundTrip() {
        String key = "pojo-key";
        User user = new User("alice", 18);
        redisOps.set(key, user, new TypeReference<User>() {}, Duration.ofMinutes(5));

        assertThat(redisOps.<User>get(key)).isEqualTo(user);
    }

    /** 泛型列表存取, 元素类型不丢失 */
    @Test
    void genericListTypePreserved() {
        String key = "list-key";
        List<User> users = List.of(new User("alice", 18), new User("bob", 20));
        redisOps.set(key, users, new TypeReference<List<User>>() {}, Duration.ofMinutes(5));

        List<User> loaded = redisOps.get(key);
        assertThat(loaded).hasSize(2);
        assertThat(loaded.get(0).getName()).isEqualTo("alice");
        assertThat(loaded.get(1).getAge()).isEqualTo(20);
    }

    /** 删除后读取为 null */
    @Test
    void delRemovesKey() {
        String key = "del-key";
        redisOps.set(key, "value", new TypeReference<String>() {}, Duration.ofMinutes(5));

        redisOps.del(key);

        assertThat(redisOps.<String>get(key)).isNull();
    }

    /** 不存在的 key 返回 null */
    @Test
    void missingKeyReturnsNull() {
        assertThat(redisOps.<String>get("missing-key")).isNull();
    }

    /** 自然过期后读取为 null, 等待时长在一秒以内 */
    @Test
    void expiresNaturally() {
        String key = "ttl-key";
        redisOps.set(key, "value", new TypeReference<String>() {}, Duration.ofMillis(500));

        await().atMost(Duration.ofSeconds(1)).untilAsserted(() ->
                assertThat(redisOps.<String>get(key)).isNull());
    }
}
