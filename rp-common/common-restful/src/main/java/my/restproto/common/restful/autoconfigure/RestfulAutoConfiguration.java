package my.restproto.common.restful.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.restproto.common.restful.tools.ResponseWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * restful 领域自动配置, 注册统一响应写入组件
 */
@AutoConfiguration
public class RestfulAutoConfiguration {

    /** 统一响应写入器 */
    @Bean
    public ResponseWriter responseWriter(ObjectMapper objectMapper) {
        return new ResponseWriter(objectMapper);
    }
}
