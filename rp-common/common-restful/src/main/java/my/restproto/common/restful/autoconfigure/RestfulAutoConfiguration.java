package my.restproto.common.restful.autoconfigure;

import my.restproto.common.restful.ResponseWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 在 servlet web 环境下装配 restful 模块的 ResponseWriter
 */
@ConditionalOnWebApplication(type = Type.SERVLET)
@AutoConfigureAfter({
        JacksonAutoConfiguration.class
})
@Import({
        ResponseWriter.class
})
@AutoConfiguration
public class RestfulAutoConfiguration {
}
