package my.restproto.common.restful.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.restproto.common.restful.exception.GlobalExceptionFilter;
import my.restproto.common.restful.exception.GlobalExceptionHandler;
import my.restproto.common.restful.tools.ResponseWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * restful 领域自动配置, 注册全局异常处理与统一响应写入组件
 */
@AutoConfiguration
public class RestfulAutoConfiguration {

    /** 全局异常处理器 */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /** 统一响应写入器 */
    @Bean
    public ResponseWriter responseWriter(ObjectMapper objectMapper) {
        return new ResponseWriter(objectMapper);
    }

    /** 全局异常过滤器 */
    @Bean
    public GlobalExceptionFilter globalExceptionFilter(ResponseWriter responseWriter) {
        return new GlobalExceptionFilter(responseWriter);
    }
}
