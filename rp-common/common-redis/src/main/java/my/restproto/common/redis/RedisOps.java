package my.restproto.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * 带类型信息的 JSON 读写, 读取时校验存储类型与请求类型一致后还原
 */
@RequiredArgsConstructor
public class RedisOps {

    /** Redis, 承担实际读写 */
    private final StringRedisTemplate stringRedisTemplate;

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /** 写入数据, 按 TypeReference 保留泛型类型, 带过期时间 */
    public <T> void set(String key, T data, TypeReference<T> type, Duration duration) {
        // 数据连同类型标识一并装箱, 读取时据此还原泛型
        Wrapper<T> wrapper = new Wrapper<>();
        wrapper.setClazz(type.getType().getTypeName());
        wrapper.setData(data);

        String json;
        try {
            json = objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Redis 序列化失败, key=" + key, e);
        }

        stringRedisTemplate.opsForValue().set(key, json, duration);
    }

    /** 读取数据, 校验存储类型与请求类型一致后按请求类型还原, 不存在返回 null */
    public <T> T get(String key, TypeReference<T> type) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            // 解析 JSON 为树结构, 提取存储的类型标识
            JsonNode wrapper = objectMapper.readTree(json);
            JsonNode clazz = wrapper.get("clazz");
            if (clazz == null) {
                throw new IllegalStateException("无法确定反序列化类型: key=" + key);
            }

            // 存储类型与请求类型不一致时拒绝, 避免类型混淆
            String storedType = clazz.asText();
            String expectedType = type.getType().getTypeName();
            if (!storedType.equals(expectedType)) {
                throw new IllegalStateException("类型不匹配, 期望 " + expectedType + ", 实际 " + storedType + ": key=" + key);
            }

            // data 为空或显式 null 时返回 null
            JsonNode data = wrapper.get("data");
            if (data == null || data.isNull()) {
                return null;
            }

            return objectMapper.readValue(data.traverse(), type);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Redis 反序列化失败: key=" + key, e);
        }
    }

    /** 删除 key */
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 数据载体, 承载类型标识与序列化数据
     */
    @Data
    @NoArgsConstructor
    public static class Wrapper<T> {

        /** 数据的 canonical 类型名, 读取时据此还原泛型 */
        private String clazz;

        /** 实际数据 */
        private T data;
    }
}
