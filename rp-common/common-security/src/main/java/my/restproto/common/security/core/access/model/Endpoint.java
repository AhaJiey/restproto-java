package my.restproto.common.security.core.access.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpMethod;

/**
 * 端点标识, HTTP 方法与模板 URL 共同定位一个端点
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint {

    /** HTTP 方法 */
    @JsonSerialize(using = ToStringSerializer.class)
    private HttpMethod method;

    /** 模板 URL, 形如 /authn/user/{id} */
    private String url;
}
