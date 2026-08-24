package my.restproto.common.redis.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import my.restproto.common.redis.RedisOps;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;

@DisplayName("RedisOps 集成测试")
@SpringBootTest(properties = {
        "restproto.lazydog.redis.enabled=true"
})
class RedisOpsIntegrationTest {

    @Autowired
    private RedisOps redisOps;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        // 清空, 保证用例间数据独立
        stringRedisTemplate.execute((RedisCallback<?>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("写入后按原类型还原")
    void shouldRoundTripValue() {
        redisOps.set("test:str", "hello", new TypeReference<String>() {}, Duration.ofMinutes(5));

        String loaded = redisOps.get("test:str", new TypeReference<String>() {});

        Assertions.assertThat(loaded).isEqualTo("hello");
    }

    @Test
    @DisplayName("泛型 List 还原")
    void shouldRoundTripGenericList() {
        List<String> data = List.of("a", "b", "c");
        redisOps.set("test:list", data, new TypeReference<List<String>>() {}, Duration.ofMinutes(5));

        List<String> loaded = redisOps.get("test:list", new TypeReference<List<String>>() {});

        Assertions.assertThat(loaded).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("删除后读取返回 null")
    void shouldReturnNullAfterDelete() {
        redisOps.set("test:del", "v", new TypeReference<String>() {}, Duration.ofMinutes(5));

        redisOps.del("test:del");

        String loaded = redisOps.get("test:del", new TypeReference<String>() {});

        Assertions.assertThat(loaded).isNull();
    }

    @Test
    @DisplayName("不存在的 key 返回 null")
    void shouldReturnNullWhenKeyAbsent() {
        String loaded = redisOps.get("test:absent", new TypeReference<String>() {});

        Assertions.assertThat(loaded).isNull();
    }

    @Test
    @DisplayName("data 为 null 时返回 null")
    void shouldReturnNullWhenDataNull() {
        redisOps.set("test:null", null, new TypeReference<String>() {}, Duration.ofMinutes(5));

        String loaded = redisOps.get("test:null", new TypeReference<String>() {});

        Assertions.assertThat(loaded).isNull();
    }

    @Test
    @DisplayName("过期后返回 null")
    void shouldReturnNullAfterExpiry() {
        redisOps.set("test:ttl", "v", new TypeReference<String>() {}, Duration.ofMillis(200));

        String loaded = redisOps.get("test:ttl", new TypeReference<String>() {});

        Assertions.assertThat(loaded).isEqualTo("v");

        // 等待 TTL 过期后 key 消失
        Awaitility.await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    String afterExpiry = redisOps.get("test:ttl", new TypeReference<String>() {});
                    Assertions.assertThat(afterExpiry).isNull();
                });
    }

    @Test
    @DisplayName("缺少类型标识抛异常")
    void shouldFailWhenClazzMissing() {
        stringRedisTemplate.opsForValue().set(
                "test:noclazz",
                """
                {
                    "data":"x"
                }
                """);

        Assertions.assertThatThrownBy(() -> redisOps.get("test:noclazz", new TypeReference<String>() {}))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("无法确定反序列化类型");
    }

    @Test
    @DisplayName("非法 JSON 抛异常")
    void shouldFailOnInvalidJson() {
        stringRedisTemplate.opsForValue().set("test:bad", "not-json");

        Assertions.assertThatThrownBy(() -> redisOps.get("test:bad", new TypeReference<String>() {}))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Redis 反序列化失败");
    }

    @Test
    @DisplayName("请求类型与存储类型不一致时抛异常")
    void shouldFailWhenTypeMismatch() {
        redisOps.set("test:mismatch", "v", new TypeReference<String>() {}, Duration.ofMinutes(5));

        Assertions.assertThatThrownBy(() -> redisOps.get("test:mismatch", new TypeReference<Integer>() {}))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("类型不匹配");
    }
}
