package my.restproto.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * Redis 操作类, 统一使用 StringRedisTemplate, value 以 JSON 存储
 */
@RequiredArgsConstructor
public class RedisOps {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /** 写入数据, 按 TypeReference 保留泛型类型, 带过期时间 */
    public <T> void set(String key, T data, TypeReference<T> type, Duration duration) {

        Wrapper<T> wrapper = new Wrapper<>();
        wrapper.setClazz(type.getType().getTypeName());
        wrapper.setData(data);

        String dataJson;
        try {
            dataJson = objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Redis 序列化失败, key=" + key, e);
        }

        stringRedisTemplate.opsForValue().set(key, dataJson , duration);
    }

    /** 读取数据, 按存储类型还原, 不存在返回 null */
    public <T> T get(String key) {
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

            // data 为空或显式 null 时返回 null
            JsonNode data = wrapper.get("data");
            if (data == null || data.isNull()) {
                return null;
            }

            // 按存储的 canonical 类型名还原 JavaType, 反序列化数据
            JavaType javaType = objectMapper.getTypeFactory().constructFromCanonical(clazz.asText());
            return objectMapper.readValue(data.traverse(), javaType);
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
        private String clazz;
        private T data;
    }
}
