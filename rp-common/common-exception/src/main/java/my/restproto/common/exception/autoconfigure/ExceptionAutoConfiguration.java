package my.restproto.common.exception.autoconfigure;

import my.restproto.common.exception.GlobalExceptionFilter;
import my.restproto.common.exception.GlobalExceptionHandler;
import my.restproto.common.restful.ResponseWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 装配全局异常处理器与异常过滤器
 */
@AutoConfiguration
public class ExceptionAutoConfiguration {

    /** 全局异常处理器 */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /** 全局异常过滤器 */
    @Bean
    public GlobalExceptionFilter globalExceptionFilter(ResponseWriter responseWriter) {
        return new GlobalExceptionFilter(responseWriter);
    }
}
